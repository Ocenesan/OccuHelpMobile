package com.example.occuhelp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class untuk setiap item grafik di dashboard
data class DashboardChartItem(
    val chartTitle: String,
    val chartData: List<BarChartRecapModel>
)

// UI State untuk Dashboard
sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(
        val chartItems: List<DashboardChartItem> // Daftar semua grafik yang akan ditampilkan
    ) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var allMcuRawData: List<McuRawDataItem> = emptyList()
    private val categoriesToDisplay = RekapitulasiViewModel().allCategories

    init {
        fetchAllDashboardCharts()
    }

    fun fetchAllDashboardCharts() {
        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            try {
                if (allMcuRawData.isEmpty()) {
                    val response = RetrofitClient.apiService.getMcuRawData()
                    if (response.isSuccessful && response.body() != null) {
                        allMcuRawData = response.body()!!
                    } else {
                        _uiState.value = DashboardUiState.Error("Gagal memuat data dasar: ${response.code()}")
                        return@launch
                    }
                }

                if (allMcuRawData.isEmpty()) {
                    _uiState.value = DashboardUiState.Error("Tidak ada data MCU untuk ditampilkan di dashboard.")
                    return@launch
                }

                val dashboardChartItems = mutableListOf<DashboardChartItem>()

                categoriesToDisplay.forEach { category ->
                    val processedData: RekapitulasiProcessedData = when (category.id) {
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
                        else -> RekapitulasiProcessedData(listOf("Error"), emptyList(), emptyList())
                    }

                    // Hanya tambahkan ke dashboard jika ada data bar chart
                    if (processedData.barChartData.isNotEmpty()) {
                        dashboardChartItems.add(
                            DashboardChartItem(
                                chartTitle = category.displayName,
                                chartData = processedData.barChartData
                            )
                        )
                    }
                }

                if (dashboardChartItems.isNotEmpty()) {
                    _uiState.value = DashboardUiState.Success(dashboardChartItems)
                } else {
                    _uiState.value = DashboardUiState.Error("Tidak ada data rekapitulasi yang bisa ditampilkan dalam grafik.")
                }

            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error("Terjadi kesalahan memuat data dashboard: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
}