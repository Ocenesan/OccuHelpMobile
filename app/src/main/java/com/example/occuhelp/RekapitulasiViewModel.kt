package com.example.occuhelp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class untuk merepresentasikan item di dropdown
data class RekapitulasiCategory(
    val id: String, // ID unik untuk kategori, bisa digunakan untuk memanggil API
    val displayName: String // Nama yang ditampilkan di dropdown
)

// UI State untuk layar rekapitulasi
sealed class RekapitulasiUiState {
    object Loading : RekapitulasiUiState()
    data class Success(
        val availableCategories: List<RekapitulasiCategory>,
        val selectedCategory: RekapitulasiCategory,
        val processedData: RekapitulasiProcessedData // Menggunakan model baru
    ) : RekapitulasiUiState()
    data class Error(val message: String) : RekapitulasiUiState()
}

class RekapitulasiViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RekapitulasiUiState>(RekapitulasiUiState.Loading)
    val uiState: StateFlow<RekapitulasiUiState> = _uiState.asStateFlow()

    // Simpan semua data mentah MCU di sini
    private var allMcuRawData: List<McuRawDataItem> = emptyList()

    val allCategories = listOf(
        RekapitulasiCategory("glukosa", "Gangguan Metabolisme Glukosa"),
        RekapitulasiCategory("status_gizi", "Gangguan Status Gizi"),
        RekapitulasiCategory("tekanan_darah", "Gangguan Tekanan Darah"),
        RekapitulasiCategory("kelompok_umur", "Kelompok Umur Peserta MCU"),
        RekapitulasiCategory("hemoglobin", "Kadar Hemoglobin"),
        RekapitulasiCategory("kreatinin", "Pemeriksaan Creatinin Darah"),
        RekapitulasiCategory("fungsi_hati", "Suspek Gangguan Fungsi Hati"),
        RekapitulasiCategory("kolesterol_total", "Pemeriksaan Kolestrol Total"),
        RekapitulasiCategory("riwayat_kesehatan", "Riwayat Kesehatan Peserta MCU"),
        RekapitulasiCategory("kolesterol_hdl", "Pemeriksaan Kolesterol HDL"),
        RekapitulasiCategory("kolesterol_ldl", "Pemeriksaan Kolesterol LDL"),
        RekapitulasiCategory("trigliserida", "Pemeriksaan Trigliserida"),
        RekapitulasiCategory("ureum", "Pemeriksaan Ureum"),
        RekapitulasiCategory("asam_urat", "Asam Urat")
    )

    // Simpan kategori terakhir yang berhasil dipilih dan diproses
    private var lastSuccessfullySelectedCategory: RekapitulasiCategory? = null

    init {
        fetchAndProcessInitialData()
    }

    private fun fetchAndProcessInitialData() {
        _uiState.value = RekapitulasiUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Ambil semua data mentah MCU
                val response = RetrofitClient.apiService.getMcuRawData()
                if (response.isSuccessful && response.body() != null) {
                    allMcuRawData = response.body()!!
                    // 2. Setelah data mentah didapat, proses untuk kategori default
                    if (allCategories.isNotEmpty() && allMcuRawData.isNotEmpty()) {
                        // Set kategori pertama sebagai yang berhasil dipilih awal
                        lastSuccessfullySelectedCategory = allCategories.first()
                        processDataForCategory(lastSuccessfullySelectedCategory!!)
                    } else if (allMcuRawData.isEmpty()){
                        _uiState.value = RekapitulasiUiState.Error("Tidak ada data MCU mentah untuk diproses.")
                    } else {
                        _uiState.value = RekapitulasiUiState.Error("Tidak ada kategori rekapitulasi tersedia.")
                    }
                } else {
                    _uiState.value = RekapitulasiUiState.Error("Gagal memuat data mentah MCU: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = RekapitulasiUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    fun onCategorySelected(category: RekapitulasiCategory) {
        if (allMcuRawData.isEmpty() && category != lastSuccessfullySelectedCategory) { // Hanya fetch ulang jika data belum ada & kategori berubah
            fetchAndProcessInitialData() // Ini akan memproses kategori default lagi
            return
        }
        // Simpan kategori yang sedang diproses ini sebagai kandidat
        // Jika proses berhasil, ini akan menjadi lastSuccessfullySelectedCategory
        processDataForCategory(category)
    }

    private fun processDataForCategory(category: RekapitulasiCategory) {
        if (allMcuRawData.isEmpty()) {
            _uiState.value = RekapitulasiUiState.Error("Data MCU mentah belum tersedia untuk kategori ${category.displayName}.")
            return
        }
        _uiState.value = RekapitulasiUiState.Loading // Tampilkan loading saat kalkulasi
        viewModelScope.launch {
            try {
                val processedResult: RekapitulasiProcessedData = when (category.id) {
                    "glukosa" -> RekapitulasiCalculator.calculateGlukosaRekap(allMcuRawData)
                    "status_gizi" -> RekapitulasiCalculator.calculateStatusGiziRekap(allMcuRawData)
                    "tekanan_darah" -> RekapitulasiCalculator.calculateTekananDarahRekap(allMcuRawData)
                    "kelompok_umur" -> RekapitulasiCalculator.calculateUmurRekap(allMcuRawData)
                    "hemoglobin" -> RekapitulasiCalculator.calculateHbRekap(allMcuRawData)
                    "kreatinin" -> RekapitulasiCalculator.calculateCreatininRekap(allMcuRawData)
                    "fungsi_hati" -> RekapitulasiCalculator.calculateFungsiHatiRekap(allMcuRawData)
                    "kolesterol_total" -> RekapitulasiCalculator.calculateKolesterolTotalRekap(allMcuRawData)
                    "riwayat_kesehatan" -> RekapitulasiCalculator.calculateRiwayatKesehatanRekap(allMcuRawData)
                    "kolesterol_hdl" -> RekapitulasiCalculator.calculateKolesterolHdlRekap(allMcuRawData)
                    "kolesterol_ldl" -> RekapitulasiCalculator.calculateKolesterolLdlRekap(allMcuRawData)
                    "trigliserida" -> RekapitulasiCalculator.calculateTrigliseridaRekap(allMcuRawData)
                    "ureum" -> RekapitulasiCalculator.calculateUreumRekap(allMcuRawData)
                    "asam_urat" -> RekapitulasiCalculator.calculateAsamUratRekap(allMcuRawData)
                    else -> RekapitulasiProcessedData(listOf("No", "Kategori", "Jumlah"), emptyList(), emptyList()) // Default kosong
                }
                lastSuccessfullySelectedCategory = category
                _uiState.value = RekapitulasiUiState.Success(
                    availableCategories = allCategories,
                    selectedCategory = category, // Gunakan kategori yang baru diproses
                    processedData = processedResult
                )
            } catch (e: Exception){
                _uiState.value = RekapitulasiUiState.Error("Gagal memproses data untuk ${category.displayName}: ${e.localizedMessage}")
            }
        }
    }
}