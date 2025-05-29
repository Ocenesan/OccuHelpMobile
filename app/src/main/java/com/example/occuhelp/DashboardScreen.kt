package com.example.occuhelp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.occuhelp.ui.OccuHelpTheme

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    userSessionViewModel: UserSessionViewModel,
    dashboardViewModel: DashboardViewModel = viewModel(),
    onMenuClicked: () -> Unit,
) {
    val userNameFromVM by userSessionViewModel.loggedInUserName.collectAsStateWithLifecycle()
    val displayName = if (!userNameFromVM.isNullOrBlank()) "Selamat Datang, \ndr. $userNameFromVM!" else "Selamat Datang!"
    val dashboardUiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Membuat seluruh Column bisa di-scroll
            .background(MaterialTheme.colorScheme.background) // Background utama layar
    ) {
        // --- Bagian Header dengan Gambar dan Teks Selamat Datang ---
        Box { // Box untuk menumpuk gambar dan tombol/teks
            Image(
                painter = painterResource(id = R.drawable.dashboard), // Pastikan drawable ini ada
                contentDescription = "Dashboard Header Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp), // Sesuaikan tinggi gambar header
                contentScale = ContentScale.Crop // Atau FillBounds, FillWidth
            )
            Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) { // Padding untuk elemen di atas gambar
                IconButton(
                    onClick = { onMenuClicked() },
                    // Modifier untuk tombol menu bisa disederhanakan jika tidak lagi pakai ConstraintLayout
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Drawer Menu",
                        tint = MaterialTheme.colorScheme.surface // Warna ikon agar kontras dengan gambar
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayName,
                    fontSize = 22.sp, // Sedikit lebih kecil mungkin
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground, // Warna teks agar kontras
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Padding umum untuk konten di bawah header
        Column(modifier = Modifier.padding(16.dp)) {
            when (val state = dashboardUiState) {
                is DashboardUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is DashboardUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { dashboardViewModel.fetchAllDashboardCharts() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                is DashboardUiState.Success -> {
                    if (state.chartItems.isEmpty()) {
                        Text(
                            "Tidak ada data ringkasan untuk ditampilkan.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        // Loop melalui semua item grafik dan tampilkan
                        state.chartItems.forEach { chartItem ->
                            BarChartDataComposable( // Gunakan Composable grafik Anda yang sudah ada
                                recapData = chartItem.chartData,
                                chartTitle = chartItem.chartTitle,
                                modifier = Modifier.padding(bottom = 24.dp) // Jarak antar grafik
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    OccuHelpTheme {
        val previewUserSessionViewModel = UserSessionViewModel().apply {
            setUser("Ayu Lestari Preview")
        }
        DashboardScreen(
            userSessionViewModel = previewUserSessionViewModel,
            dashboardViewModel = viewModel(),
            onMenuClicked = {}
        )
    }
}