package com.example.occuhelp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.yml.charts.axis.DataCategoryOptions
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RekapitulasiScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: RekapitulasiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dropdownExpanded by remember { mutableStateOf(false) }

    Column( // Gunakan Column sebagai root untuk scrolling yang lebih mudah dikelola
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // --- Header ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier
                    .size(44.dp)
                    .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Pastikan ikon benar
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Rekapitulasi",
                style = MaterialTheme.typography.headlineLarge
            )
        }
        // --- Akhir Header ---

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is RekapitulasiUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RekapitulasiUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(), // Mengisi ruang yang tersedia di Column
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        viewModel.onCategorySelected(
                            viewModel.allCategories.first()
                        )
                    }) {
                        Text("Coba Lagi")
                    }
                }
            }
            is RekapitulasiUiState.Success -> {
                // Dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                    modifier = Modifier.fillMaxWidth() // Dropdown mengambil lebar penuh
                ) {
                    OutlinedTextField(
                        value = state.selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pilih Kategori Rekapitulasi") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        state.availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
                                onClick = {
                                    viewModel.onCategorySelected(category)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Menggunakan Column untuk menampung Grafik dan Tabel agar bisa di-scroll bersama
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Bar Chart
                    if (state.processedData.barChartData.isNotEmpty()) {
                        BarChartDataComposable(
                            recapData = state.processedData.barChartData,
                            chartTitle = state.selectedCategory.displayName // Judul grafik dari kategori
                        )
                    } else {
                        Text(
                            "Tidak ada data grafik untuk ditampilkan.",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Table Layout
                    if (state.processedData.tableRows.isNotEmpty()) {
                        TableLayoutRecap(
                            headers = state.processedData.tableHeaders,
                            rows = state.processedData.tableRows
                        )
                    } else {
                        Text(
                            "Tidak ada data tabel untuk ditampilkan.",
                            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Padding di bawah tabel
                }
            }
        }
    }
}

@Composable
fun TableLayoutRecap(
    headers: List<String>, // Terima header dinamis
    rows: List<RekapitulasiDisplayRow>, // Terima baris data yang sudah diproses
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            headers.forEachIndexed { index, headerText ->
                val weight = when (headers.size) { // Atur weight berdasarkan jumlah kolom
                    3 -> if (index == 0) 0.7f else if (index == 1) 2.3f else 1f // No, Kategori, Jumlah
                    4 -> if (index == 0) 0.7f else if (index == 1 || index == 2) 1.5f else 0.8f // No, JK, Kat, Jml
                    else -> 1f // Default
                }
                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(weight),
                    textAlign = if (index == 0 || index == headers.size -1 ) TextAlign.Center else TextAlign.Start
                )
            }
        }
        Column {
            rows.forEach { item -> // Loop biasa karena sudah di dalam Column yang scrollable
                TableListItemRecap(
                    item = item,
                    headersCount = headers.size,
                    isEvenRow = item.no % 2 == 0
                )
            }
        }
    }
}

@Composable
fun TableListItemRecap(
    item: RekapitulasiDisplayRow,
    headersCount: Int,
    isEvenRow: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isEvenRow) MaterialTheme.colorScheme.outlineVariant
                else MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.3f)
            )
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.no.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(
                when (headersCount) {
                    3 -> 0.7f
                    4 -> 0.7f
                    else -> 1f
                }
            ),
            textAlign = TextAlign.Center
        )
        // Tampilkan semua values dari RekapitulasiDisplayRow
        item.values.forEachIndexed { index, value ->
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(
                    when (headersCount) {
                        3 -> if (index == 0) 2.3f else 0f // Kolom kategori utama
                        4 -> if (index == 0 || index == 1) 1.5f else 0f // Kolom jenis kelamin & kategori
                        else -> 1f
                    }
                )
            )
        }
        Text(
            text = item.jumlah.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(
                when (headersCount) {
                    3 -> 1f
                    4 -> 0.8f
                    else -> 1f
                }
            ),
            textAlign = TextAlign.Center
        )
    }
}

data class BarChartRecapModel(
    val label: String,
    val point: Point,
    val description: String
)

@Composable
fun BarChartDataComposable( // Ganti nama
    modifier: Modifier = Modifier,
    recapData: List<BarChartRecapModel>, // Terima data yang sudah siap untuk grafik
    chartTitle: String // Tambahkan judul untuk grafik
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large) // Background Card
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = chartTitle, // Judul dinamis
                style = MaterialTheme.typography.displayMedium, // Sesuaikan style
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.inverseOnSurface),
                shape = MaterialTheme.shapes.large,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Weekly",
                        color = Color(0xFF217F96),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Panah Bawah",
                        tint = Color(0xFF0086B0),
                        modifier = Modifier

                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (recapData.isEmpty()) {
            Box( // Gunakan Box untuk menengahkan Text
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Beri tinggi yang sama dengan grafik agar layout konsisten
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Data grafik tidak tersedia.")
            }
            return
        }

        val barStyle = BarStyle(
            barWidth = 20.dp,
            cornerRadius = 4.dp,
            paddingBetweenBars = 50.dp
        )

        // Sumbu X: Label diambil dari BarChartRecapModel.label
        val xAxisData = AxisData.Builder()
            .startDrawPadding(25.dp)
            .axisOffset(20.dp)
            .axisStepSize(barStyle.barWidth + barStyle.paddingBetweenBars)
            .steps(recapData.size -1) // Jumlah step adalah jumlah data - 1
            .bottomPadding(20.dp) // Kurangi padding bawah jika label panjang
            .endPadding(0.dp)
            .labelAndAxisLinePadding(8.dp)
            .axisLabelAngle(0f) // Miringkan label jika panjang
            .labelData { index ->
                val label = recapData.getOrNull(index)?.label ?: ""
                // Jika label masih terlalu panjang, potong atau buat dua baris jika library mendukung
                if (label.length > 5 && recapData.size > 6) label.take(4) + ".."
                else if (label.length > 7) label.take(6) + ".."
                else label
            }
            .axisLineColor(MaterialTheme.colorScheme.outline)
            .axisLabelColor(MaterialTheme.colorScheme.onSurface)
            .startDrawPadding(barStyle.barWidth / 2 + barStyle.paddingBetweenBars / 2)
            .shouldDrawAxisLineTillEnd(true)
            .bottomPadding(20.dp)
            .build()

        // Sumbu Y: Perlu dihitung secara dinamis
        val maxJumlah = recapData.maxOfOrNull { it.point.y } ?: 1f
        val yStepCount = 5
        // Hitung nilai tertinggi untuk sumbu Y agar sedikit di atas bar tertinggi
        val actualYAxisMaxValue = if (maxJumlah == 0f) 50f else ceil(maxJumlah / yStepCount.toFloat()) * yStepCount
//        val yStepSize = determineYStepSize(maxJumlah) // Fungsi helper untuk menentukan step Y
//        val yStepsCount = if (yStepSize > 0) (ceil(maxJumlah / yStepSize)).toInt() else 1

        val yAxisData = AxisData.Builder()
            .labelAndAxisLinePadding(20.dp)
            .steps(yStepCount -1) // Minimal 1 step
            .labelData { index ->
                val value = index * (actualYAxisMaxValue / yStepCount.toFloat())
                if (value >= 1000) "%.1f".format(value / 1000f) else value.toInt().toString()
            }
            .labelAndAxisLinePadding(15.dp)
            .axisLineColor(MaterialTheme.colorScheme.outline)
            .axisLabelColor(MaterialTheme.colorScheme.onBackground)
            .backgroundColor(MaterialTheme.colorScheme.background) // Background Y-axis transparan
            .topPadding(30.dp)
            .startDrawPadding(8.dp)
            .build()

        val topBar = recapData.maxByOrNull { it.point.y }

        val barChartLibData = BarChartData(
            chartData = recapData.map { bar ->
                BarData(
                    point = bar.point,
                    color = if (bar == topBar && bar.point.y > 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary,
                    description = bar.description, // Deskripsi untuk tooltip/anotasi
                    label = bar.label,
                    dataCategoryOptions = DataCategoryOptions() // Tambahkan jika perlu kustomisasi per kategori
                )
            },
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            paddingEnd = 0.dp,
            barStyle = barStyle,
            backgroundColor = MaterialTheme.colorScheme.background, // Warna background grafik
            paddingTop = 16.dp, // Padding di dalam area grafik
            tapPadding = 10.dp // Area tap untuk interaksi
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Sesuaikan tinggi grafik
                // .clip(RoundedCornerShape(16.dp)) // Tidak perlu jika Column parent sudah di-clip
                .background(MaterialTheme.colorScheme.background) // Warna background grafik
        ) {
            BarChart(
                modifier = Modifier.fillMaxSize(),
                barChartData = barChartLibData
            )

            // Anotasi untuk bar tertinggi (opsional, styling perlu disesuaikan)
            /*topBar?.let { bar ->
                if (bar.point.y > 0 && recapData.isNotEmpty()) { // Hanya tampilkan jika ada nilai
                     Box(
                         modifier = Modifier
                             .align(Alignment.TopCenter)
                             .fillMaxWidth()
                             .padding(top = 4.dp)
                     ) {
                         Text(
                             text = bar.description,
                             color = MaterialTheme.colorScheme.onBackground,
                             style = MaterialTheme.typography.labelSmall,
                             modifier = Modifier
                                 .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                 .padding(horizontal = 4.dp, vertical = 3.dp)
                         )
                     }
                }
            }*/
        }
    }
}

// Fungsi helper untuk menentukan step pada sumbu Y
/*private fun determineYStepSize(maxValue: Float): Float {
    if (maxValue <= 0) return 10f // Default jika tidak ada data atau semua 0
    return when {
        maxValue <= 50 -> 10f
        maxValue <= 100 -> 20f
        maxValue <= 200 -> 40f
        maxValue <= 500 -> 100f
        maxValue <= 1000 -> 200f
        maxValue <= 2000 -> 400f
        maxValue <= 3000 -> 1000f
        else -> 2000f
    }
}*/

@Preview(showBackground = true)
@Composable
fun RekapitulasiScreenPreview() {
    OccuHelpTheme {
        RekapitulasiScreen(
            onNavigateBack = {}
        )
    }
}

