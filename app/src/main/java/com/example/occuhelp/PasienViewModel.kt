package com.example.occuhelp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.ceil

sealed class PasienUiState {
    object Loading : PasienUiState()
    data class Success(
        val paginatedPatients: List<Patient>, // Pasien untuk halaman saat ini
        val currentPage: Int,
        val totalPages: Int
    ) : PasienUiState()
    data class Error(val message: String) : PasienUiState()
}

class PasienViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PasienUiState>(PasienUiState.Loading)
    val uiState: StateFlow<PasienUiState> = _uiState.asStateFlow()
    private var allPatients: List<Patient> = emptyList() // Semua pasien dari API
    private var filteredPatients: List<Patient> = emptyList() // Pasien setelah difilter (untuk pencarian)

    private val itemsPerPage = 3 // Jumlah pasien per halaman
    private var currentPage = 1 // Halaman saat ini (dimulai dari 1)

    init {
        fetchPatients()
    }

    fun fetchPatients() {
        _uiState.value = PasienUiState.Loading
        currentPage = 1
        viewModelScope.launch {
            try {
                // TODO: Tambahkan header otentikasi jika diperlukan oleh API
                // Contoh: val token = dataStoreManager.getAuthToken().firstOrNull()
                // if (token == null) { _uiState.value = PasienUiState.Error("User not authenticated"); return@launch }
                // val authHeader = "Bearer $token"

                val response = RetrofitClient.apiService.getPatients() // Panggil fungsi API
                // .getPatients(authHeader) // Jika perlu token
                if (response.isSuccessful && response.body() != null) {
                    allPatients = response.body()!!.patients
                    filteredPatients = allPatients // Awalnya, hasil filter sama dengan semua pasien
                    updatePaginatedData()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    println("API Error getPatients: ${response.code()} - $errorBody")
                    _uiState.value = PasienUiState.Error("Gagal memuat data pasien: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Exception getPatients: ${e.localizedMessage}")
                e.printStackTrace()
                _uiState.value = PasienUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    fun searchPatients(query: String) {
        currentPage = 1
        if (query.isBlank()) {
            filteredPatients = allPatients
        } else {
            filteredPatients = allPatients.filter { patient ->
                patient.name.contains(query, ignoreCase = true) ||
                        patient.medRecordId.contains(query, ignoreCase = true)
            }
        }
        updatePaginatedData()
    }

    private fun updatePaginatedData() {
        if (filteredPatients.isEmpty()) {
            _uiState.value = PasienUiState.Success(emptyList(), 1, 1)
            return
        }

        val totalItems = filteredPatients.size
        val totalPages = ceil(totalItems.toDouble() / itemsPerPage.toDouble()).toInt().coerceAtLeast(1)

        // Pastikan currentPage valid
        if (currentPage > totalPages) {
            currentPage = totalPages
        }
        if (currentPage < 1) {
            currentPage = 1
        }

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = (startIndex + itemsPerPage).coerceAtMost(totalItems)

        val paginatedList = if (startIndex < endIndex) {
            filteredPatients.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
        _uiState.value = PasienUiState.Success(paginatedList, currentPage, totalPages)
    }

    fun goToPage(page: Int) {
        val currentState = _uiState.value
        if (currentState is PasienUiState.Success) {
            if (page >= 1 && page <= currentState.totalPages) {
                currentPage = page
                updatePaginatedData()
            }
        }
    }

    fun nextPage() {
        val currentState = _uiState.value
        if (currentState is PasienUiState.Success) {
            if (currentPage < currentState.totalPages) {
                currentPage++
                updatePaginatedData()
            }
        }
    }

    fun previousPage() {
        if (currentPage > 1) {
            currentPage--
            updatePaginatedData()
        }
    }
}