package com.example.occuhelp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor // Alias untuk android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Event untuk UI
sealed class ReportEvent {
    data class PdfGenerated(val filePath: String) : ReportEvent()
    data class ErrorGeneratingPdf(val message: String) : ReportEvent()
    object AllChartsCaptured : ReportEvent()
    object ProcessedDataReadyForCapture : ReportEvent()
}

// Data untuk satu kategori dalam laporan
data class ReportCategoryData(
    val categoryInfo: RekapitulasiCategory, // Menyimpan info kategori (ID dan nama tampilan)
    val barChartData: List<BarChartRecapModel>,
    val tableHeaders: List<String>,
    val tableRows: List<RekapitulasiDisplayRow>
)

// State untuk data yang akan dicetak, sekarang list dari kategori
data class ReportPrintData(
    val allCategoryData: List<ReportCategoryData>,
    val dateRange: String
)

class ReportViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Data yang sudah diproses dan siap untuk dicetak (termasuk data untuk grafik dan tabel)
    private val _processedReportData = MutableStateFlow<ReportPrintData?>(null)
    val processedReportData: StateFlow<ReportPrintData?> = _processedReportData.asStateFlow()

    // State untuk kategori grafik yang sedang aktif di-render untuk di-capture
    private val _categoryForCapture = MutableStateFlow<ReportCategoryData?>(null)
    val categoryForCapture: StateFlow<ReportCategoryData?> = _categoryForCapture.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ReportEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allMcuRawData: List<McuRawDataItem> = emptyList()
    // Daftar semua kategori yang akan diproses untuk laporan
    val categoriesForReport: List<RekapitulasiCategory> = RekapitulasiViewModel().allCategories

    // State untuk tanggal yang dipilih
    val selectedStartDate = MutableStateFlow<Long?>(null) // Simpan sebagai timestamp
    val selectedEndDate = MutableStateFlow<Long?>(null)

    // Map untuk menyimpan bitmap yang sudah di-capture
    private val capturedBitmaps = mutableMapOf<String, Bitmap>()
    private var currentCaptureIndex = -1 // Untuk melacak kategori mana yang sedang di-capture

    init {
        fetchRawDataIfNeeded() // Ambil data mentah saat init
    }

    private fun fetchRawDataIfNeeded() {
        if (allMcuRawData.isNotEmpty()) return
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getMcuRawData()
                if (response.isSuccessful && response.body() != null) {
                    allMcuRawData = response.body()!!
                    android.util.Log.d("ReportVM", "Raw MCU data fetched successfully: ${allMcuRawData.size} items")
                } else {
                    android.util.Log.e("ReportVM", "Gagal memuat data dasar: ${response.code()}")
                    _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Gagal memuat data dasar untuk laporan."))
                }
            } catch (e: Exception) {
                android.util.Log.e("ReportVM", "Exception fetching raw MCU data: ${e.localizedMessage}", e)
                _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Error data dasar: ${e.localizedMessage}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // prepareReportData sekarang memproses semua kategori
    fun prepareAllCategoriesReportData(startDate: Long?, endDate: Long?) {
        if (startDate == null || endDate == null || startDate > endDate) {
            viewModelScope.launch { _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Rentang tanggal tidak valid.")) }
            return
        }
        // Pastikan data mentah sudah ada, jika belum coba fetch sekali lagi
        if (allMcuRawData.isEmpty()) {
            viewModelScope.launch {
                fetchRawDataIfNeeded() // Coba fetch
                if (allMcuRawData.isEmpty()) { // Cek lagi setelah fetch (walaupun fetch asinkron)
                    _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Data mentah belum siap atau gagal dimuat."))
                    return@launch
                }
            }
            if (allMcuRawData.isEmpty()){
                viewModelScope.launch{_eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Data dasar tidak tersedia."))}
                return
            }
        }


        _isLoading.value = true
        _processedReportData.value = null // Reset data sebelumnya
        viewModelScope.launch {
            try {
                val dateFormatFromApi = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                dateFormatFromApi.timeZone = TimeZone.getTimeZone("WIB")
                val filteredData = allMcuRawData.filter { rawItem ->
                    try {
                        val examDateString = rawItem.examinationDate
                        if (examDateString.isNullOrBlank()) return@filter false
                        val examTimestamp = dateFormatFromApi.parse(examDateString)?.time
                        examTimestamp != null && examTimestamp in startDate..endDate
                    } catch (e: Exception) { false }
                }

                android.util.Log.d("ReportVM", "Data filtered by date range: ${filteredData.size} items")

                if (filteredData.isEmpty()) {
                    _processedReportData.value = ReportPrintData(
                        allCategoryData = emptyList(),
                        dateRange = "${formatTimestamp(startDate)} - ${formatTimestamp(endDate)}"
                    )
                    _isLoading.value = false
                    _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Tidak ada data MCU pada periode tanggal yang dipilih."))
                    return@launch
                }

                val allProcessedCategoryData = mutableListOf<ReportCategoryData>()
                categoriesForReport.forEach { category ->
                    val processedResult: RekapitulasiProcessedData = when (category.id) {
                        "glukosa" -> RekapitulasiCalculator.calculateGlukosaRekap(filteredData)
                        "status_gizi" -> RekapitulasiCalculator.calculateStatusGiziRekap(filteredData)
                        "tekanan_darah" -> RekapitulasiCalculator.calculateTekananDarahRekap(filteredData)
                        "kelompok_umur" -> RekapitulasiCalculator.calculateUmurRekap(filteredData)
                        "hemoglobin" -> RekapitulasiCalculator.calculateHbRekap(filteredData)
                        "kreatinin" -> RekapitulasiCalculator.calculateCreatininRekap(filteredData)
                        "fungsi_hati" -> RekapitulasiCalculator.calculateFungsiHatiRekap(filteredData)
                        "kolesterol_total" -> RekapitulasiCalculator.calculateKolesterolTotalRekap(filteredData)
                        "riwayat_kesehatan" -> RekapitulasiCalculator.calculateRiwayatKesehatanRekap(filteredData)
                        "kolesterol_hdl" -> RekapitulasiCalculator.calculateKolesterolHdlRekap(filteredData)
                        "kolesterol_ldl" -> RekapitulasiCalculator.calculateKolesterolLdlRekap(filteredData)
                        "trigliserida" -> RekapitulasiCalculator.calculateTrigliseridaRekap(filteredData)
                        "ureum" -> RekapitulasiCalculator.calculateUreumRekap(filteredData)
                        "asam_urat" -> RekapitulasiCalculator.calculateAsamUratRekap(filteredData)
                        else -> RekapitulasiProcessedData(listOf("Error"), emptyList(), emptyList())
                    }
                    // Hanya tambahkan jika ada data bar chart atau tabel untuk kategori ini
                    if (processedResult.barChartData.isNotEmpty() || processedResult.tableRows.isNotEmpty()) {
                        allProcessedCategoryData.add(
                            ReportCategoryData(
                                categoryInfo = category,
                                barChartData = processedResult.barChartData,
                                tableHeaders = processedResult.tableHeaders,
                                tableRows = processedResult.tableRows
                            )
                        )
                    }
                }
                android.util.Log.d("ReportVM", "All categories processed. Total data items: ${allProcessedCategoryData.size}")

                _processedReportData.value = ReportPrintData(
                    allCategoryData = allProcessedCategoryData,
                    dateRange = "${formatTimestamp(startDate)} - ${formatTimestamp(endDate)}"
                )
                // Setelah data siap, kirim event ke UI untuk memulai proses capture
                if (allProcessedCategoryData.any { it.barChartData.isNotEmpty() }) {
                    _eventFlow.emit(ReportEvent.ProcessedDataReadyForCapture)
                } else {
                    // Tidak ada grafik, mungkin langsung generate PDF atau emit AllChartsCaptured
                    _eventFlow.emit(ReportEvent.AllChartsCaptured) // Jika hanya tabel
                }

            } catch (e: Exception) {
                android.util.Log.e("ReportVM", "Error preparing report data: ${e.localizedMessage}", e)
                _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Gagal memproses data laporan: ${e.localizedMessage}"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dipanggil dari UI setelah event ProcessedDataReadyForCapture diterima
    fun startChartCaptureProcess() {
        val dataToProcess = _processedReportData.value
        if (dataToProcess == null || dataToProcess.allCategoryData.isEmpty()) {
            viewModelScope.launch { _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Tidak ada data kategori untuk di-capture.")) }
            return
        }
        val categoriesWithCharts = dataToProcess.allCategoryData.filter { it.barChartData.isNotEmpty() }
        if (categoriesWithCharts.isEmpty()) {
            android.util.Log.d("ReportVM", "No charts to capture, proceeding to PDF generation.")
            viewModelScope.launch { _eventFlow.emit(ReportEvent.AllChartsCaptured) } // Langsung ke PDF jika tidak ada grafik
            return
        }

        _isLoading.value = true // Loading selama proses capture
        capturedBitmaps.clear()
        currentCaptureIndex = 0
        android.util.Log.d("ReportVM", "Starting chart capture process for ${categoriesWithCharts.size} charts.")
        captureNextChartActual(categoriesWithCharts)
    }

    // Fungsi rekursif/iteratif untuk capture
    private fun captureNextChartActual(categoriesToCapture: List<ReportCategoryData>) {
        if (currentCaptureIndex >= categoriesToCapture.size) {
            android.util.Log.d("ReportVM", "All charts captured.")
            _categoryForCapture.value = null // Reset
            viewModelScope.launch { _eventFlow.emit(ReportEvent.AllChartsCaptured) }
            // _isLoading akan di-set false setelah PDF dibuat atau gagal
            return
        }
        val categoryDataToCapture = categoriesToCapture[currentCaptureIndex]
        android.util.Log.d("ReportVM", "Requesting capture for: ${categoryDataToCapture.categoryInfo.displayName}")
        _categoryForCapture.value = categoryDataToCapture // Set kategori untuk di-render dan di-capture oleh UI
    }

    fun onChartBitmapCaptured(categoryId: String, bitmap: Bitmap?) {
        val dataToProcess = _processedReportData.value
        if (dataToProcess == null) return

        if (bitmap != null) {
            capturedBitmaps[categoryId] = bitmap
            android.util.Log.d("ReportVM", "Bitmap captured and stored for $categoryId.")
        } else {
            android.util.Log.w("ReportVM", "Failed to capture bitmap for $categoryId, will be skipped in PDF.")
        }

        currentCaptureIndex++
        val categoriesWithCharts = dataToProcess.allCategoryData.filter { it.barChartData.isNotEmpty() }
        captureNextChartActual(categoriesWithCharts) // Lanjut ke grafik berikutnya
    }

    fun generatePdfWithCapturedCharts(context: Context) {
        val reportData = _processedReportData.value
        if (reportData == null) { // Cek juga jika reportData null
            viewModelScope.launch { _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Tidak ada data untuk membuat PDF.")) }
            return
        }
        if (reportData.allCategoryData.isEmpty() && reportData.dateRange.isNotBlank()){
            // Kasus dimana filter tanggal menghasilkan data kosong, tapi user tetap ingin cetak (misal, PDF kosong dengan header)
            // Atau, kita bisa mencegah ini dengan tidak memanggil generate jika allCategoryData kosong
            viewModelScope.launch { _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Tidak ada data rekapitulasi untuk dicetak pada periode ini.")) }
            return
        }

        // Panggil fungsi yang lama, yang sekarang menggunakan Map untuk bitmaps
        generatePdfFromAllCategories(context, reportData, capturedBitmaps)
    }

    // Fungsi ini akan dipanggil dari Composable yang men-capture Bitmap
    fun generatePdfFromAllCategories(
        context: Context,
        reportData: ReportPrintData,
        // Kita perlu List<Bitmap?> jika ingin capture semua grafik
        // Untuk saat ini, kita akan menggambar grafik secara terprogram jika memungkinkan
        // atau menerima Map<String, Bitmap?> (categoryId ke Bitmap)
        // Untuk kesederhanaan, kita akan coba gambar teks saja untuk grafik di PDF
        // atau Anda bisa memodifikasi untuk menerima List<Bitmap?> yang urutannya sesuai allCategoryData
        capturedBitmaps: Map<String, Bitmap?>? = null // Map dari category.id ke Bitmap
    ) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) { // Operasi file di IO dispatcher
            try {
                val pdfDocument = PdfDocument()
                val pageHeight = 842 // A4
                val pageWidth = 595  // A4
                val leftMargin = 40f
                val rightMargin = pageWidth - 40f
                val topMargin = 40f
                val bottomMargin = 40f
                val contentWidth = rightMargin - leftMargin
                val lineSpacing = 15f
                val sectionSpacing = 30f

                val titlePaint = Paint().apply { color = AndroidColor.BLACK; textSize = 16f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textAlign = Paint.Align.CENTER }
                val subTitlePaint = Paint().apply { color = AndroidColor.BLACK; textSize = 12f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); textAlign = Paint.Align.LEFT }
                val textPaint = Paint().apply { color = AndroidColor.BLACK; textSize = 10f }
                val tableHeaderPaint = Paint(textPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD) }

                var currentPageNumber = 1
                var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                var page = pdfDocument.startPage(pageInfo)
                var canvas = page.canvas
                var yPosition = topMargin

                // Fungsi untuk memulai halaman baru
                fun startNewPage() {
                    pdfDocument.finishPage(page)
                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = topMargin
                }

                // Judul Laporan Global
                canvas.drawText("Laporan Rekapitulasi MCU", pageWidth / 2f, yPosition, titlePaint)
                yPosition += lineSpacing
                canvas.drawText("Periode: ${reportData.dateRange}", pageWidth / 2f, yPosition, textPaint.apply{textAlign = Paint.Align.CENTER})
                yPosition += sectionSpacing

                reportData.allCategoryData.forEach { categoryData ->
                    // Cek apakah butuh halaman baru sebelum memulai seksi kategori
                    // (Tinggi perkiraan untuk judul seksi + sedikit ruang)
                    if (yPosition + sectionSpacing + 100 > pageHeight - bottomMargin) { // Perkiraan tinggi minimal untuk grafik/tabel
                        startNewPage()
                    }

                    // Judul Kategori
                    canvas.drawText(categoryData.categoryInfo.displayName, leftMargin, yPosition, subTitlePaint)
                    yPosition += lineSpacing * 1.5f

                    // Gambar Grafik (jika ada bitmap yang sesuai)
                    val chartBitmap = capturedBitmaps?.get(categoryData.categoryInfo.id)
                    if (chartBitmap != null && categoryData.barChartData.isNotEmpty()) {
                        val chartHeightOnPdf = 150f // Tinggi grafik di PDF, sesuaikan
                        if (yPosition + chartHeightOnPdf > pageHeight - bottomMargin) startNewPage()

                        val aspectRatio = chartBitmap.width.toFloat() / chartBitmap.height.toFloat()
                        val chartPdfWidth = (contentWidth * 0.8f).coerceAtMost(chartBitmap.width.toFloat()) // Ambil 80% lebar konten atau lebar asli bitmap
                        val chartPdfActualHeight = chartPdfWidth / aspectRatio
                        val chartRect = Rect(leftMargin.toInt(), yPosition.toInt(), (leftMargin + chartPdfWidth).toInt(), (yPosition + chartPdfActualHeight).toInt())
                        canvas.drawBitmap(chartBitmap, null, chartRect, null)
                        yPosition += chartPdfActualHeight + lineSpacing
                        // chartBitmap.recycle() // Jangan recycle jika bitmap ini mungkin digunakan lagi atau dari cache
                    } else if (categoryData.barChartData.isNotEmpty()) {
                        // Gambar placeholder jika tidak ada bitmap tapi ada data
                        if (yPosition + lineSpacing > pageHeight - bottomMargin) startNewPage()
                        canvas.drawText("(Grafik untuk ${categoryData.categoryInfo.displayName} tidak di-render)", leftMargin, yPosition, textPaint.apply { color = AndroidColor.GRAY })
                        yPosition += lineSpacing
                    }

                    // Gambar Tabel
                    if (categoryData.tableRows.isNotEmpty()) {
                        if (yPosition + lineSpacing * 2 > pageHeight - bottomMargin) startNewPage() // Cek ruang untuk header tabel

                        val colWidths = calculateColumnWidths(categoryData.tableHeaders.size, contentWidth)
                        var currentX = leftMargin
                        categoryData.tableHeaders.forEachIndexed { index, header ->
                            canvas.drawText(header, currentX + 3, yPosition, tableHeaderPaint)
                            currentX += colWidths[index]
                        }
                        yPosition += lineSpacing / 2
                        canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, tableHeaderPaint)
                        yPosition += lineSpacing

                        categoryData.tableRows.forEach { row ->
                            if (yPosition + lineSpacing > pageHeight - bottomMargin) {
                                startNewPage()
                                // Gambar ulang header tabel di halaman baru
                                currentX = leftMargin
                                categoryData.tableHeaders.forEachIndexed { index, header ->
                                    canvas.drawText(header, currentX + 3, yPosition, tableHeaderPaint)
                                    currentX += colWidths[index]
                                }
                                yPosition += lineSpacing / 2
                                canvas.drawLine(leftMargin, yPosition, rightMargin, yPosition, tableHeaderPaint)
                                yPosition += lineSpacing
                            }

                            currentX = leftMargin
                            // Asumsi RekapitulasiDisplayRow.values adalah List<String>
                            // dan urutannya sesuai dengan header setelah "No" dan sebelum "Jumlah"
                            val displayValues = mutableListOf<String>()
                            displayValues.add(row.no.toString()) // Kolom No
                            displayValues.addAll(row.values)     // Kolom dari values
                            displayValues.add(row.jumlah.toString()) // Kolom Jumlah

                            displayValues.forEachIndexed { index, cellValue ->
                                if (index < colWidths.size) { // Pastikan tidak out of bounds
                                    canvas.drawText(cellValue, currentX + 3, yPosition, textPaint)
                                    currentX += colWidths[index]
                                }
                            }
                            yPosition += lineSpacing
                        }
                    }
                    yPosition += sectionSpacing // Jarak antar kategori
                }

                pdfDocument.finishPage(page)

                // Simpan PDF
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val fileName = "LaporanRekapitulasiMCU_${System.currentTimeMillis()}.pdf"
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { pdfDocument.writeTo(it) }
                pdfDocument.close()

                withContext(Dispatchers.Main) {
                    _eventFlow.emit(ReportEvent.PdfGenerated(file.absolutePath))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    _eventFlow.emit(ReportEvent.ErrorGeneratingPdf("Gagal membuat PDF: ${e.localizedMessage}"))
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun calculateColumnWidths(numColumns: Int, totalWidth: Float): List<Float> {
        if (numColumns == 0) return emptyList()
        // Pembagian sederhana, bisa dibuat lebih canggih berdasarkan konten
        // Misal: No kecil, Kategori besar, Jumlah sedang
        if (numColumns == 3) { // No, Kategori, Jumlah
            return listOf(totalWidth * 0.15f, totalWidth * 0.55f, totalWidth * 0.30f)
        }
        if (numColumns == 4) { // No, JK, Kategori, Jumlah
            return listOf(totalWidth * 0.1f, totalWidth * 0.25f, totalWidth * 0.4f, totalWidth * 0.25f)
        }
        val widthPerColumn = totalWidth / numColumns
        return List(numColumns) { widthPerColumn }
    }

    private fun formatTimestamp(timestamp: Long?, format: String = "dd MMM yyyy"): String {
        return timestamp?.let { SimpleDateFormat(format, Locale.getDefault()).format(Date(it)) } ?: "-"
    }

    // Panggil ini jika user membatalkan atau proses PDF selesai/gagal
    // untuk membersihkan state capture.
    fun clearCaptureState() {
        _categoryForCapture.value = null
        _processedReportData.value = null // Reset data yang diproses agar tidak memicu capture ulang
        capturedBitmaps.clear()
        currentCaptureIndex = -1
        _isLoading.value = false // Pastikan loading juga false
    }
}