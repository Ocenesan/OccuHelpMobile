package com.example.occuhelp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ForgotPasswordEvent {
    data class ShowFeedback(val message: String, val isError: Boolean) : ForgotPasswordEvent()
    object NavigateBackToLogin : ForgotPasswordEvent()
}

class ForgotPasswordViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Menggunakan SharedFlow untuk event seperti Toast atau navigasi
    private val _eventFlow = MutableSharedFlow<ForgotPasswordEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // Jika ingin dialog error yang sama, Anda bisa menggunakan MutableStateFlow<LoginPopUpType?>
    private val _apiError = MutableStateFlow<LoginPopUpType?>(null)
    val apiError: StateFlow<LoginPopUpType?> = _apiError.asStateFlow()

    fun onSendLinkClicked(email: String, context: Context) {
        _apiError.value = null // Reset error jika menggunakan state error

        if (!isNetworkAvailable(context)) {
            viewModelScope.launch { _eventFlow.emit(ForgotPasswordEvent.ShowFeedback("Tidak ada koneksi internet.", true)) }
            _apiError.value = LoginPopUpType.NETWORK_ERROR
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            viewModelScope.launch { _eventFlow.emit(ForgotPasswordEvent.ShowFeedback("Masukkan alamat email yang valid.", true)) }
            _apiError.value = LoginPopUpType.EMPTY_TEXTFIELD
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = ResetLinkRequest(email)
                val response = RetrofitClient.apiService.sendResetLink(request) // Pastikan URL benar

                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()!!
                    val message = baseResponse.message ?: "Link reset telah dikirim ke email Anda."
                    _eventFlow.emit(ForgotPasswordEvent.ShowFeedback(message, false))
                    _eventFlow.emit(ForgotPasswordEvent.NavigateBackToLogin)
                } else {
                    val errorBody = response.errorBody()?.string()
                    var serverMessage = "Gagal mengirim link reset. Coba lagi nanti."
                    if (!errorBody.isNullOrBlank()) {
                        try {
                            val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                            serverMessage = baseResponse.message ?: baseResponse.error ?: serverMessage
                        } catch (e: Exception) {
                            println("Failed to parse reset link error body: $e")
                        }
                    }
                    println("Send reset link API error: ${response.code()} - ${response.message()} - Body: $errorBody")
                    _eventFlow.emit(ForgotPasswordEvent.ShowFeedback(serverMessage, true))
                    _apiError.value = LoginPopUpType.NETWORK_ERROR
                }
            } catch (e: Exception) {
                _eventFlow.emit(ForgotPasswordEvent.ShowFeedback("Terjadi kesalahan: ${e.localizedMessage}", true))
                _apiError.value = LoginPopUpType.NETWORK_ERROR
                println("Send reset link exception: ${e.localizedMessage}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // fun dismissApiErrorDialog() {
    //     _apiError.value = null
    // }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean { // Duplikasi, idealnya di utility class
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
}