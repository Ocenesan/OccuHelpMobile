package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Fungsi helper untuk format tanggal (bisa ditaruh di file util terpisah)
fun formatDateString(isoDateString: String?, outputFormat: String = "dd MMMM yyyy"): String {
    if (isoDateString.isNullOrBlank()) return "-"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Pastikan parsing dari UTC
        val date = inputFormat.parse(isoDateString)
        val output = SimpleDateFormat(outputFormat, Locale.getDefault())
        output.timeZone = TimeZone.getDefault() // Format ke timezone lokal
        date?.let { output.format(it) } ?: "-"
    } catch (e: Exception) {
        // Coba format YYYY-MM-DD jika format ISO gagal (misalnya untuk examination_date)
        try {
            val inputFormatSimple = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormatSimple.parse(isoDateString)
            val output = SimpleDateFormat(outputFormat, Locale.getDefault())
            date?.let { output.format(it) } ?: "-"
        } catch (e2: Exception) {
            e.printStackTrace()
            e2.printStackTrace()
            isoDateString // Kembalikan string asli jika semua parsing gagal
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPasienScreen(
    onNavigateBack: () -> Unit = {},
    detailPasienViewModel: DetailPasienViewModel = viewModel()
) {
    val uiState by detailPasienViewModel.uiState.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        val (
            backButton, patientTitle, profileImage,
            patientNameRow, biodataDivider, detailsColumn, mcuButtonRow,
            loadingIndicator, errorDisplay
        ) = createRefs()

        // Top App Bar Elements
        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier
                .size(44.dp)
                .background(
                    OccuHelpBackButtonBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = "Detail Pasien",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(patientTitle) {
                top.linkTo(backButton.top)
                bottom.linkTo(backButton.bottom)
                start.linkTo(backButton.end, margin = 16.dp)
            }
        )

        // --- Konten Utama berdasarkan UI State ---
        when (val state = uiState) {
            is DetailPasienUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.constrainAs(loadingIndicator) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
            is DetailPasienUiState.Success -> {
                val patient = state.patient

                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24), // Ganti dengan gambar dinamis jika ada
                    contentDescription = "Foto Pasien",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(1.dp, color = Color.Black, CircleShape)
                        .constrainAs(profileImage) {
                            top.linkTo(backButton.bottom, margin = 24.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(patientNameRow) {
                            top.linkTo(profileImage.bottom, margin = 20.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    Text(
                        text = patient.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = "#Biodata Pasien",
                        color = Color.White, // Sebaiknya gunakan warna dari MaterialTheme.colorScheme
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary, // Ganti ke warna tema
                                shape = MaterialTheme.shapes.small // Sesuaikan shape
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                HorizontalDivider(
                    // ... modifier biodataDivider ...
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp) // Beri padding lebih
                        .constrainAs(biodataDivider) {
                            top.linkTo(patientNameRow.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .constrainAs(detailsColumn) {
                            top.linkTo(biodataDivider.bottom, margin = 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                ) {
                    DetailRow("Tgl. Pemeriksaan", formatDateString(patient.examDate))
                    DetailRow("Nomor Rekam Medis", patient.medRecordId)
                    DetailRow("Nomor Pasien", patient.patientId) // Gunakan patientIdString
                    DetailRow("Jenis Kelamin", patient.gender)
                    DetailRow("Umur", "${patient.age} Tahun")
                    DetailRow("Tanggal Lahir", formatDateString(patient.birthDate))
                    // Alamat Lengkap tidak ada di data class Patient Anda, perlu ditambahkan jika ada
                    // DetailRow("Alamat Lengkap", "Jl. Cempaka Putih Timur No. 10, Jakarta")
                }

                // Tombol Lihat Hasil MCU
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(mcuButtonRow) {
                            top.linkTo(detailsColumn.bottom, margin = 32.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom, margin = 24.dp)
                            width = Dimension.fillToConstraints
                        },
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { /* Action */ },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .widthIn(min = 180.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = "MCU Icon",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lihat Hasil MCU",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
            is DetailPasienUiState.Error -> {
                Column(
                    modifier = Modifier.constrainAs(errorDisplay) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { detailPasienViewModel.retryFetchPatientDetail() }) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
    }
}

// Fungsi pembantu untuk baris biodata (tidak banyak perubahan, sudah cukup baik)
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPasienScreenPreview() {
    OccuHelpTheme {
        DetailPasienScreen(onNavigateBack = {})
    }
}