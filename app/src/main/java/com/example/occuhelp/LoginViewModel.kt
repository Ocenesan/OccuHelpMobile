package com.example.occuhelp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginEvent {
    data class NavigateToHome(val userName: String) : LoginEvent()
    object NavigateToForgotPassword : LoginEvent()
}

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<LoginPopUpType?>(null)
    val loginError: StateFlow<LoginPopUpType?> = _loginError.asStateFlow()

    // Menggunakan SharedFlow untuk event yang hanya ingin diobservasi sekali (seperti navigasi)
    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent = _loginEvent.asSharedFlow() // Gunakan asSharedFlow untuk membuatnya read-only dari luar

    private val _loggedInUserName = MutableStateFlow<String?>(null)

    fun onLoginClicked(nip: String, password: String, context: Context) {
        _loginError.value = null // Reset error

        if (!isNetworkAvailable(context)) {
            _loginError.value = LoginPopUpType.NETWORK_ERROR
            return
        }

        if (nip.isBlank() || password.isBlank()) {
            _loginError.value = LoginPopUpType.EMPTY_TEXTFIELD
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(nip, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    _loggedInUserName.value = authResponse.user.name
                    println("Login successful: Token: ${authResponse.token}, User: ${authResponse.user.name}")
                    // TODO: Simpan token (misal di SharedPreferences atau DataStoreViewModel)
                    // Contoh: dataStoreManager.saveAuthToken(authResponse.token)

                    _loginEvent.emit(LoginEvent.NavigateToHome(authResponse.user.name))
                } else {
                    val errorBody = response.errorBody()?.string()
                    var serverMessage = "NIP atau kata sandi salah."
                    if (!errorBody.isNullOrBlank()) {
                        try {
                            val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                            serverMessage = baseResponse.message ?: baseResponse.error ?: serverMessage
                        } catch (e: Exception) {
                            println("Failed to parse login error body: $e")
                        }
                    }
                    println("Login API error: ${response.code()} - ${response.message()} - Body: $errorBody")
                    // Anda bisa membuat LoginPopUpType yang lebih spesifik jika ingin menampilkan pesan dari server
                    _loginError.value = LoginPopUpType.WRONG_CREDENTIALS // Pesan dialog akan tetap dari enum
                    // Jika ingin pesan custom di dialog, LoginPopUpType perlu diubah atau buat event toast
                    // _loginEvent.emit(LoginEvent.ShowToast(serverMessage))
                }
            } catch (e: Exception) {
                _loginError.value = LoginPopUpType.NETWORK_ERROR
                println("Login exception: ${e.localizedMessage}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onForgotPasswordClicked() {
        viewModelScope.launch {
            _loginEvent.emit(LoginEvent.NavigateToForgotPassword)
        }
    }

    fun dismissErrorDialog() {
        _loginError.value = null
    }

    fun performLogoutCleanup() {
        _loggedInUserName.value = null
        // TODO: Hapus token otentikasi yang tersimpan (dari DataStore/SharedPreferences)
        // Contoh jika Anda memiliki DataStoreManager:
        // viewModelScope.launch {
        //     dataStoreManager.clearAuthToken()
        // }
        println("User session data cleared (simulated).")
    }

    // Fungsi helper untuk memeriksa ketersediaan jaringan (bisa dipindahkan ke utility class)
    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean {
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