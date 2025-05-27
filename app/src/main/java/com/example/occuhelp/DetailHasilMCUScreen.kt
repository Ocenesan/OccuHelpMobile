package com.example.occuhelp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Fungsi formatDateString (jika belum ada atau pindahkan ke file util)
fun formatResultDate(isoDateString: String?, outputFormat: String = "dd MMMM yyyy"): String {
    if (isoDateString.isNullOrBlank()) return "-"
    return try {
        // Coba format dengan waktu dan zona Z
        val inputFormatWithZ = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormatWithZ.timeZone = TimeZone.getTimeZone("UTC")
        var date = inputFormatWithZ.parse(isoDateString)

        if (date == null) {
            // Coba format tanpa milliseconds dan Z
            val inputFormatNoMillis = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            inputFormatNoMillis.timeZone = TimeZone.getTimeZone("UTC") // Asumsi input UTC jika ada T
            date = inputFormatNoMillis.parse(isoDateString)
        }

        if (date == null) {
            // Coba format YYYY-MM-DD
            val inputFormatSimple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            date = inputFormatSimple.parse(isoDateString)
        }

        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        output.timeZone = TimeZone.getDefault() // Format ke timezone lokal
        date?.let { output.format(it) } ?: isoDateString // Kembalikan string asli jika semua parsing gagal
    } catch (e: Exception) {
        e.printStackTrace()
        isoDateString // Kembalikan string asli jika parsing gagal
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailHasilMCUScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    viewModel: DetailHasilMCUViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        val (backButton, divider, tableLayout, patientTitle, biodataRow, loading, errorView, guideline1, guideline2, guideline3) = createRefs()

        val topGuideline = createGuidelineFromTop(0.02f)
        val startGuideline = createGuidelineFromStart(0.03f)
        val endGuideline = createGuidelineFromEnd(0.03f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.White)
                .constrainAs(guideline1) {
                    top.linkTo(topGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.White)
                .constrainAs(guideline2) {
                    start.linkTo(startGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(2.dp)
                .background(Color.White)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        // Back Button
        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .size(44.dp)
                .background(
                    OccuHelpBackButtonBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OccuHelpBackButtonIcon,
                modifier = Modifier.size(28.dp)
            )

        }

        // Patient Name (Judul)
        Text(
            text = "Hasil MCU Pasien",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(patientTitle) {
                top.linkTo(topGuideline)
                start.linkTo(backButton.end, margin = 16.dp)
                bottom.linkTo(backButton.bottom)
            }
        )
        when (val state = uiState) {
            is McuResultUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.constrainAs(loading) {
                    top.linkTo(parent.top); bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start); end.linkTo(parent.end)
                })
            }
            is McuResultUiState.Error -> {
                Column(
                    modifier = Modifier.constrainAs(errorView) {
                        top.linkTo(parent.top); bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start); end.linkTo(parent.end)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.retryFetch() }) {
                        Text("Coba Lagi")
                    }
                }
            }
            is McuResultUiState.Success -> {
                // Tampilkan Nama Pasien dari ViewModel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(biodataRow) {
                            top.linkTo(patientTitle.bottom, margin = 24.dp) // Atur margin dari patientTitle
                            start.linkTo(parent.start) // Gunakan parent start/end, bukan guideline
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(
                        text = state.patientName ?: "Nama Pasien Tidak Tersedia",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f), // Agar bisa wrap jika panjang
                    )
                    Text(
                        text = "#Biodata Pasien", // Ganti label jika perlu
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, // Ganti warna background label
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                HorizontalDivider( // Ganti ke HorizontalDivider M3
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp,
                    modifier = Modifier
                        .constrainAs(divider) {
                            top.linkTo(biodataRow.bottom, margin = 12.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(vertical = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .constrainAs(tableLayout) {
                            top.linkTo(divider.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom) // Biarkan mengisi sisa ruang
                            height = Dimension.fillToConstraints // Penting untuk LazyColumn
                        }
                ) {
                    if (state.results.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada hasil MCU untuk pasien ini.")
                        }
                    } else {
                        TableLayoutMCU(mcuResults = state.results, modifier = Modifier.fillMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
fun TableLayoutMCU(
    mcuResults: List<McuResult>, // Terima List<McuResult>
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Table Header
        Row(
            modifier = Modifier // Hapus modifier dari parameter di sini
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) // Warna header lebih soft
                .padding(vertical = 12.dp, horizontal = 8.dp), // Tambah padding horizontal
            verticalAlignment = Alignment.CenterVertically // Pusatkan teks di header
        ) {
            Text(
                text = "No.",
                style = MaterialTheme.typography.titleSmall, // Style untuk header
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.7f), // Sesuaikan weight
                textAlign = TextAlign.Center
            )
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(2.5f), // Sesuaikan weight
            )
            Text(
                text = "Hasil Analisa", // Ganti dari "Hasil Analisa"
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1.8f), // Sesuaikan weight
                textAlign = TextAlign.Center // Pusatkan hasil jika perlu
            )
            Text( // Tambah kolom tanggal hasil
                text = "Tanggal",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(2f), // Sesuaikan weight
                textAlign = TextAlign.Center
            )
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(mcuResults.size) { index -> // Gunakan index untuk nomor urut
                val resultItem = mcuResults[index]
                TableListItemMCU(
                    number = index + 1, // Nomor urut dimulai dari 1
                    mcuResult = resultItem,
                    isEvenRow = index % 2 == 0
                )
            }
        }
        // Tombol "Lihat Detail" mungkin tidak relevan di sini karena ini sudah halaman detail hasil.
        // Jika ada aksi lain, letakkan di sini.
    }
}

@Composable
fun TableListItemMCU(
    number: Int,
    mcuResult: McuResult,
    isEvenRow: Boolean, // Untuk zebra striping
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isEvenRow) MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.inverseSurface // Atau Color.Transparent
            )
            .padding(vertical = 10.dp, horizontal = 8.dp), // Sesuaikan padding
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number.toString(),
            modifier = Modifier
                .weight(0.7f)
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = mcuResult.category,
            modifier = Modifier.weight(2.5f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = mcuResult.result,
            modifier = Modifier.weight(1.8f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = formatResultDate(mcuResult.resultDate, "dd/MM/yy"), // Format tanggal lebih pendek
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailHasilMCUScreenPreview() {
    OccuHelpTheme {
        DetailHasilMCUScreen(onNavigateBack = {})
    }
}