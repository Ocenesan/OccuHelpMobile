// HasilMCUViewModel.kt
package com.example.occuhelp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

// UI State sekarang menggunakan McuPatient untuk list utama
sealed class HasilMCUUiState {
    object Loading : HasilMCUUiState()
    data class Success(
        val paginatedMcuPatients: List<McuPatient>, // List dari McuPatient
        val currentPage: Int,
        val totalPages: Int
    ) : HasilMCUUiState()
    data class Error(val message: String) : HasilMCUUiState()
}

class HasilMCUViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<HasilMCUUiState>(HasilMCUUiState.Loading)
    val uiState: StateFlow<HasilMCUUiState> = _uiState.asStateFlow()

    private var allMcuPatients: List<McuPatient> = emptyList()
    private var filteredMcuPatients: List<McuPatient> = emptyList()

    private val itemsPerPage = 4
    private var currentPage = 1

    init {
        fetchMcuPatientsList()
    }

    fun fetchMcuPatientsList() {
        _uiState.value = HasilMCUUiState.Loading
        currentPage = 1
        viewModelScope.launch {
            try {
                // TODO: Tambahkan otentikasi jika perlu
                val response = RetrofitClient.apiService.getMcuPatients()
                android.util.Log.d("HASIL_MCU_VM", "API getMcuPatients Response Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    allMcuPatients = response.body()!!
                    filteredMcuPatients = allMcuPatients
                    android.util.Log.d("HASIL_MCU_VM", "Fetched ${allMcuPatients.size} McuPatients.")
                    allMcuPatients.take(2).forEach { android.util.Log.d("HASIL_MCU_VM", "Sample McuPatient: $it") }
                    updatePaginatedData()
                } else {
                    val errorMsg = "Gagal memuat daftar hasil MCU: ${response.code()} - ${response.errorBody()?.string()}"
                    android.util.Log.e("HASIL_MCU_VM", errorMsg)
                    _uiState.value = HasilMCUUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                android.util.Log.e("HASIL_MCU_VM", "Exception fetching McuPatients: ${e.localizedMessage}", e)
                _uiState.value = HasilMCUUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    fun searchMcuResults(query: String) {
        currentPage = 1
        filteredMcuPatients = if (query.isBlank()) {
            allMcuPatients
        } else {
            allMcuPatients.filter { mcuPatient ->
                mcuPatient.name.contains(query, ignoreCase = true) ||
                        mcuPatient.examDate.contains(query, ignoreCase = true) ||
                        mcuPatient.examType.contains(query, ignoreCase = true) ||
                        mcuPatient.status.name.contains(query, ignoreCase = true)
                // Tambahkan filter berdasarkan patientId jika field itu juga string
                // atau medRecordId jika ada di McuPatient
            }
        }
        updatePaginatedData()
    }

    private fun updatePaginatedData() {
        if (filteredMcuPatients.isEmpty()) {
            _uiState.value = HasilMCUUiState.Success(emptyList(), 1, 1)
            return
        }
        val totalItems = filteredMcuPatients.size
        val totalPages = ceil(totalItems.toDouble() / itemsPerPage.toDouble()).toInt().coerceAtLeast(1)
        if (currentPage > totalPages) currentPage = totalPages
        if (currentPage < 1) currentPage = 1

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(totalItems)

        val paginatedList = if (startIndex < endIndex) filteredMcuPatients.subList(startIndex, endIndex) else emptyList()
        _uiState.value = HasilMCUUiState.Success(paginatedList, currentPage, totalPages)
    }

    // Fungsi goToPage, nextPage, previousPage tetap sama, bekerja pada currentPage dan memanggil updatePaginatedData
    fun goToPage(page: Int) {
        val currentState = _uiState.value
        if (currentState is HasilMCUUiState.Success) {
            if (page >= 1 && page <= currentState.totalPages) {
                currentPage = page; updatePaginatedData()
            }
        }
    }
    fun nextPage() {
        val currentState = _uiState.value
        if (currentState is HasilMCUUiState.Success && currentPage < currentState.totalPages) {
            currentPage++; updatePaginatedData()
        }
    }
    fun previousPage() {
        if (currentPage > 1) {
            currentPage--; updatePaginatedData()
        }
    }
}