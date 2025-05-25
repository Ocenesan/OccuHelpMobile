package com.example.occuhelp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class McuResultUiState {
    object Loading : McuResultUiState()
    // Kita juga butuh info pasien untuk ditampilkan di header layar ini
    data class Success(val patientName: String?, val results: List<McuResult>) : McuResultUiState()
    data class Error(val message: String) : McuResultUiState()
}

class DetailHasilMCUViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<McuResultUiState>(McuResultUiState.Loading)
    val uiState: StateFlow<McuResultUiState> = _uiState.asStateFlow()

    private val patientId: Int //= checkNotNull(savedStateHandle["patientId"])
    // Kita juga perlu nama pasien, yang bisa diteruskan sebagai argumen atau diambil terpisah
    // Untuk kesederhanaan, kita akan asumsikan nama pasien juga diteruskan sebagai argumen
    private val patientName: String? //= savedStateHandle["patientName"]

    init {
        // Ambil dan Log argumen
        val pIdFromHandle: Int? = savedStateHandle["patientId"]
        val pNameFromHandle: String? = savedStateHandle["patientName"]

        android.util.Log.d("DETAIL_MCU_VM", "Received patientId: $pIdFromHandle, patientName: '$pNameFromHandle'")

        if (pIdFromHandle != null && pIdFromHandle > 0) {
            this.patientId = pIdFromHandle
            this.patientName = pNameFromHandle // Bisa null jika nama tidak wajib
            fetchMcuResults(this.patientId)
        } else {
            android.util.Log.e("DETAIL_MCU_VM", "Invalid patientId received: $pIdFromHandle")
            _uiState.value = McuResultUiState.Error("ID Pasien tidak valid untuk mengambil hasil MCU.")
            this.patientId = 0 // Atau nilai default penanda error
            this.patientName = null
        }
    }

//    init {
//        if (patientId > 0) {
//            fetchMcuResults(patientId)
//        } else {
//            _uiState.value = McuResultUiState.Error("ID Pasien tidak valid untuk mengambil hasil MCU.")
//        }
//    }

    fun fetchMcuResults(pId: Int) {
        _uiState.value = McuResultUiState.Loading
        viewModelScope.launch {
            try {
                // TODO: Tambahkan header otentikasi jika diperlukan
                // Asumsi API langsung mengembalikan List<McuResult>
                val response = RetrofitClient.apiService.getMcuResultsByPatient(pId)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = McuResultUiState.Success(patientName, response.body()!!)
                } else {
                    // Jika API membungkus list dalam objek (misal McuResultsResponse)
                    // if (response.isSuccessful && response.body() != null) {
                    //     _uiState.value = McuResultUiState.Success(patientName, response.body()!!.results) // atau response.body()!!.namaPropertiList
                    // }
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    _uiState.value = McuResultUiState.Error("Gagal memuat hasil MCU: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = McuResultUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    fun retryFetch() {
        if (patientId > 0) {
            fetchMcuResults(patientId)
        }
    }
}