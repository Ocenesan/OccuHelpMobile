package com.example.occuhelp

import android.content.Intent
import android.graphics.Canvas as AndroidCanvas
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.graphics.createBitmap

// Data dummy untuk histori laporan (akan diganti dengan data dari ViewModel/database nanti)
val dataReportDummy = listOf(
    Triple(1, "Rekap Status Gizi - 01 Jan 2023 - 15 Jan 2023.pdf", "15/01/2023"),
    Triple(2, "Rekap Tekanan Darah - 10 Feb 2023 - 20 Feb 2023.pdf", "20/02/2023")
)

// Fungsi helper untuk memformat timestamp ke string tanggal
fun formatLongToDateString(timestamp: Long?, pattern: String = "dd MMM yyyy"): String {
    if (timestamp == null) return "Pilih Tanggal"
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(Date(timestamp))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: ReportViewModel = viewModel() // Gunakan ReportViewModel
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val reportPrintDataToProcess by viewModel.processedReportData.collectAsStateWithLifecycle() // Ganti ke processedReportData
    val categoryForCapture by viewModel.categoryForCapture.collectAsStateWithLifecycle() // Untuk status capture

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val startDateMillis by viewModel.selectedStartDate.collectAsStateWithLifecycle()
    val endDateMillis by viewModel.selectedEndDate.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var reportHistory by remember { mutableStateOf(dataReportDummy) }

    // Listener untuk event dari ViewModel (PDF generated atau error)
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ReportEvent.PdfGenerated -> {
                    val newEntry = Triple(
                        reportHistory.size + 1,
                        File(event.filePath).name,
                        formatLongToDateString(System.currentTimeMillis(), "dd/MM/yyyy")
                    )
                    reportHistory = listOf(newEntry) + reportHistory

                    scope.launch { // Pastikan showSnackbar dipanggil dalam CoroutineScope
                        val result = snackbarHostState.showSnackbar(
                            message = "PDF Laporan disimpan!",
                            duration = SnackbarDuration.Long,
                            actionLabel = "Buka"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            try {
                                val file = File(event.filePath)
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Tidak dapat membuka PDF.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    viewModel.clearCaptureState() // Bersihkan state setelah PDF selesai
                }
                is ReportEvent.ErrorGeneratingPdf -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Error: ${event.message}", duration = SnackbarDuration.Long)
                    }
                    viewModel.clearCaptureState() // Bersihkan state jika ada error
                }
                is ReportEvent.ProcessedDataReadyForCapture -> {
                    // Data siap, mulai proses capture grafik
                    viewModel.startChartCaptureProcess()
                }
                is ReportEvent.AllChartsCaptured -> {
                    // Semua grafik di-capture, generate PDF
                    viewModel.generatePdfWithCapturedCharts(context)
                }
            }
        }
    }

    // --- Composable untuk Capture Grafik (di luar layar) ---
    if (categoryForCapture != null && isLoading) {
        // Ambil data grafik yang sesuai untuk kategori yang sedang di-capture
        val chartDataForCapture = categoryForCapture!!.barChartData // Non-null assertion karena categoryForCapture tidak null
        val chartTitleForCapture = categoryForCapture!!.categoryInfo.displayName

        AndroidView(
            factory = { ctx ->
                ComposeView(ctx).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        (600 * ctx.resources.displayMetrics.density).toInt(), // Lebar capture bisa lebih besar
                        (400 * ctx.resources.displayMetrics.density).toInt()  // Tinggi capture bisa lebih besar
                    )
                }
            },
            // Modifier untuk memindahkan ComposeView ke luar layar
            modifier = Modifier.offset(x = 10000.dp, y = 10000.dp),
            update = { composeView ->
                android.util.Log.d("ReportScreen", "AndroidView update for: $chartTitleForCapture")
                composeView.setContent {
                    OccuHelpTheme {
                        // Gunakan BarChartDataComposable yang sudah ada
                        BarChartDataComposable(
                            recapData = chartDataForCapture,
                            chartTitle = chartTitleForCapture // Judul bisa ditampilkan atau tidak saat capture
                        )
                    }
                }
                // Capture setelah ada sedikit delay untuk rendering
                scope.launch {
                    delay(700) // Tambah delay sedikit jika grafik kompleks
                    try {
                        if (composeView.width > 0 && composeView.height > 0) {
                            val bitmap = createBitmap(composeView.width, composeView.height)
                            val canvas = AndroidCanvas(bitmap)
                            composeView.draw(canvas)
                            viewModel.onChartBitmapCaptured(categoryForCapture!!.categoryInfo.id, bitmap)
                            android.util.Log.d("ReportScreen", "Bitmap captured for: $chartTitleForCapture")
                        } else {
                            viewModel.onChartBitmapCaptured(categoryForCapture!!.categoryInfo.id, null)
                            android.util.Log.e("ReportScreen", "ComposeView size 0 for: $chartTitleForCapture")
                        }
                    } catch (e: Exception) {
                        viewModel.onChartBitmapCaptured(categoryForCapture!!.categoryInfo.id, null)
                        android.util.Log.e("ReportScreen", "Error capturing bitmap for $chartTitleForCapture", e)
                    }
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState()), // Agar seluruh konten bisa di-scroll
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier
                    .size(44.dp)
                    .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Report",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bagian Tengah: Input Periode Tanggal
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally // Tengahkan elemen di dalam Column ini
        ) {
            Text(
                text = "Periode Tanggal Input",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Pemilih Tanggal Mulai
            Button(
                onClick = { showStartDatePicker = true },
                modifier = Modifier.height(40.dp).width(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(), // Mengisi seluruh area Button
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Area Teks
                    Box(
                        modifier = Modifier
                            .weight(1f) // Mengambil sisa ruang setelah ikon
                            .fillMaxHeight() // Mengisi tinggi Row
                            // Gunakan warna dari Theme.kt, misal secondaryContainer atau warna kustom
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .padding(horizontal = 16.dp), // Padding untuk teks
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = formatLongToDateString(startDateMillis, "dd MMM yyyy"),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium // Sesuaikan style jika perlu
                        )
                    }

                    // Area Ikon
                    Box(
                        modifier = Modifier
                            // Lebar bisa diatur atau wrap content. Padding akan memberi ruang.
                            .fillMaxHeight() // Mengisi tinggi Row
                            // Gunakan warna lebih terang dari Theme.kt, misal surfaceVariant atau inverseOnSurface jika itu terang
                            .background(MaterialTheme.colorScheme.outlineVariant) // Warna lebih terang untuk area ikon
                            .padding(horizontal = 12.dp), // Padding di sekitar ikon
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal",
                            // Tint untuk ikon, misal primary atau onSurfaceVariant
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp) // Ukuran ikon
                        )
                    }
                }
            }
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = startDateMillis ?: Calendar.getInstance().timeInMillis,
                    yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR)) // Batasi tahun
                )
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.selectedStartDate.value = datePickerState.selectedDateMillis
                            showStartDatePicker = false
                        }) { Text("Pilih") }
                    },
                    dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") } }
                ) { DatePicker(state = datePickerState) }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sampai dengan",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Pemilih Tanggal Akhir
            Button(
                onClick = { showEndDatePicker = true },
                modifier = Modifier.height(40.dp).width(200.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(), // Mengisi seluruh area Button
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Area Teks
                    Box(
                        modifier = Modifier
                            .weight(1f) // Mengambil sisa ruang setelah ikon
                            .fillMaxHeight() // Mengisi tinggi Row
                            // Gunakan warna dari Theme.kt, misal secondaryContainer atau warna kustom
                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                            .padding(horizontal = 16.dp), // Padding untuk teks
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = formatLongToDateString(endDateMillis, "dd MMM yyyy"),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium // Sesuaikan style jika perlu
                        )
                    }

                    // Area Ikon
                    Box(
                        modifier = Modifier
                            // Lebar bisa diatur atau wrap content. Padding akan memberi ruang.
                            .fillMaxHeight() // Mengisi tinggi Row
                            // Gunakan warna lebih terang dari Theme.kt, misal surfaceVariant atau inverseOnSurface jika itu terang
                            .background(MaterialTheme.colorScheme.outlineVariant) // Warna lebih terang untuk area ikon
                            .padding(horizontal = 12.dp), // Padding di sekitar ikon
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pilih Tanggal",
                            // Tint untuk ikon, misal primary atau onSurfaceVariant
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp) // Ukuran ikon
                        )
                    }
                }
            }
            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = endDateMillis ?: Calendar.getInstance().timeInMillis,
                    yearRange = (2020..Calendar.getInstance().get(Calendar.YEAR)),
                    selectableDates = object : SelectableDates { // Pastikan tanggal akhir tidak sebelum tanggal mulai
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return startDateMillis?.let { utcTimeMillis >= it } != false
                        }
                        override fun isSelectableYear(year: Int): Boolean {
                            return year <= Calendar.getInstance().get(Calendar.YEAR)
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.selectedEndDate.value = datePickerState.selectedDateMillis
                            showEndDatePicker = false
                        }) { Text("Pilih") }
                    },
                    dismissButton = { TextButton(onClick = { showEndDatePicker = false }) { Text("Batal") } }
                ) { DatePicker(state = datePickerState) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Histori Laporan Terunduh",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tabel Histori (menggunakan data dummy untuk saat ini)
        if (reportHistory.isEmpty()){
            Text("Belum ada laporan yang dicetak.", modifier = Modifier.padding(vertical=16.dp))
        } else {
            TableLayoutReport(reportHistory) // Menggunakan dataReportDummy yang di-state
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End // Tombol di ujung kanan
        ) {
            OutlinedButton(
                onClick = {
                    if (startDateMillis != null && endDateMillis != null) {
                        // Panggil fungsi untuk menyiapkan data semua kategori
                        viewModel.prepareAllCategoriesReportData(startDateMillis, endDateMillis)
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Pilih rentang tanggal dengan lengkap.") }
                    }
                },
                enabled = !isLoading && startDateMillis != null && endDateMillis != null,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar ikon dan teks
                ) {
                    if (isLoading && reportPrintDataToProcess == null) { // Tampilkan loading hanya saat prepare data awal
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp), // Sesuaikan ukuran agar pas dengan ikon
                            color = MaterialTheme.colorScheme.primary, // Warna disesuaikan dengan konteks tombol
                            strokeWidth = 2.dp
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = "Icon Report",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Cetak Laporan",
                        style = MaterialTheme.typography.bodyLarge, // Atau labelLarge jika lebih sesuai untuk tombol
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading && reportPrintDataToProcess != null) { // Loading saat generate PDF
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Membuat PDF...")
            }
        }
    }
}

@Composable
fun TableLayoutReport(
    reportHistory: List<Triple<Int, String, String>>, // Terima histori
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "No.",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
            Text(
                "Nama File",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )
            Text(
                "Tanggal",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.Center)
        }

        if (reportHistory.isEmpty()) {
            // Tidak perlu ditampilkan jika sudah ada pengecekan di ReportScreen
        } else {
            reportHistory.forEach { item -> // Ganti ke forEach jika tidak butuh lazy scrolling di sini
                TableReportListItem(
                    id = item.first,
                    downloadedFile = item.second,
                    date = item.third
                )
            }
        }
    }
}

@Composable
fun TableReportListItem(
    id: Int,
    downloadedFile: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (id % 2 == 0) MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.outlineVariant // Atau Color.Transparent
            )
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            id.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(0.5f),
            textAlign = TextAlign.Center
        )
        Text(
            downloadedFile,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(2f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    OccuHelpTheme {
        ReportScreen(onNavigateBack = {})
    }
}