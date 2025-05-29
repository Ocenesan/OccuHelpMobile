package com.example.occuhelp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class UbahPasswordEvent {
    data class ShowFeedback(val message: String, val isError: Boolean) : UbahPasswordEvent()
    object NavigateToPasswordUpdated : UbahPasswordEvent()
}

class UbahPasswordViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UbahPasswordEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // Ambil token dan email dari argumen navigasi
    val token: String = checkNotNull(savedStateHandle[Screen.UbahPassword.tokenArg])
    val email: String = checkNotNull(savedStateHandle[Screen.UbahPassword.emailArg])

    init {
        android.util.Log.d("UBAH_PASS_VM", "Token: $token, Email: $email")
    }

    fun onUpdatePasswordClicked(password: String, passwordConfirmation: String) {
        if (password.isBlank() || passwordConfirmation.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UbahPasswordEvent.ShowFeedback("Password tidak boleh kosong.", true)) }
            return
        }
        if (password != passwordConfirmation) {
            viewModelScope.launch { _uiEvent.emit(UbahPasswordEvent.ShowFeedback("Konfirmasi password tidak cocok.", true)) }
            return
        }
        // TODO: Tambahkan validasi panjang password jika ada aturan dari backend

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = ResetPasswordRequest(
                    token = token,
                    email = email,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
                val response = RetrofitClient.apiService.resetPassword(request)

                if (response.isSuccessful && response.body() != null) {
                    val successMessage = response.body()!!.message ?: "Password berhasil diubah."
                    _uiEvent.emit(UbahPasswordEvent.ShowFeedback(successMessage, false))
                    _uiEvent.emit(UbahPasswordEvent.NavigateToPasswordUpdated)
                } else {
                    val errorBody = response.errorBody()?.string()
                    var serverMessage = "Gagal mengubah password. Coba lagi."
                    if (!errorBody.isNullOrBlank()) {
                        try {
                            val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                            serverMessage = baseResponse.message ?: baseResponse.error ?: serverMessage
                        } catch (e: Exception) { /* Abaikan error parsing, gunakan pesan default */ }
                    }
                    _uiEvent.emit(UbahPasswordEvent.ShowFeedback(serverMessage, true))
                }
            } catch (e: Exception) {
                _uiEvent.emit(UbahPasswordEvent.ShowFeedback("Terjadi kesalahan: ${e.localizedMessage}", true))
            } finally {
                _isLoading.value = false
            }
        }
    }
}