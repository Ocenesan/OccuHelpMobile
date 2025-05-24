package com.example.occuhelp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed class untuk merepresentasikan UI State Detail Pasien
sealed class DetailPasienUiState {
    object Loading : DetailPasienUiState()
    data class Success(val patient: Patient) : DetailPasienUiState() // Patient dari OccuEntity.kt
    data class Error(val message: String) : DetailPasienUiState()
}

class DetailPasienViewModel(
    savedStateHandle: SavedStateHandle // Untuk mengambil patientId dari argumen navigasi
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailPasienUiState>(DetailPasienUiState.Loading)
    val uiState: StateFlow<DetailPasienUiState> = _uiState.asStateFlow()

    // Ambil patientId dari argumen navigasi yang diteruskan melalui SavedStateHandle
    // Pastikan nama argumennya ("patientId") sama dengan yang didefinisikan di NavGraph
    private val patientId: Int = try {
        checkNotNull(savedStateHandle["patientId"])
    } catch (e: IllegalStateException) {
        // Handle kasus jika patientId tidak ada di SavedStateHandle, misal dengan nilai default atau error state
        // Ini seharusnya tidak terjadi jika navigasi selalu mengirimkan ID.
        _uiState.value = DetailPasienUiState.Error("ID Pasien tidak ditemukan saat inisialisasi.")
        0 // Atau nilai penanda error lain, atau lempar exception jika ini kondisi fatal
    }

    init {
        if (patientId > 0) { // Pastikan ID valid sebelum fetch
            fetchPatientDetailInternal()
        } else {
            _uiState.value = DetailPasienUiState.Error("ID Pasien tidak valid.")
        }
    }

    fun retryFetchPatientDetail() {
        if (patientId > 0) {
            fetchPatientDetailInternal()
        } else {
            // Jika patientId masih tidak valid (dari constructor gagal), tidak melakukan apa-apa
            // atau perbarui UI State ke error lagi jika perlu.
            _uiState.value = DetailPasienUiState.Error("Tidak bisa mencoba lagi: ID Pasien tidak valid.")
        }
    }

    // Fungsi internal untuk logika fetch sebenarnya
    private fun fetchPatientDetailInternal() {
        _uiState.value = DetailPasienUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getPatientById(patientId) // Gunakan properti patientId

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = DetailPasienUiState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = DetailPasienUiState.Error("Gagal memuat detail pasien: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = DetailPasienUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }
}