package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme // Pastikan ini diimpor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HasilMCUScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onNavigateToDetailMCU: (patientId: Int, patientName: String?) -> Unit,
    viewModel: HasilMCUViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(searchText) {
        viewModel.searchMcuResults(searchText)
    }

    LaunchedEffect(key1 = (uiState as? HasilMCUUiState.Success)?.currentPage) {
        val successState = uiState as? HasilMCUUiState.Success
        if (successState != null && successState.paginatedMcuPatients.isNotEmpty()) {
            if (lazyListState.layoutInfo.totalItemsCount > 0) {
                lazyListState.scrollToItem(0)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Gunakan PasienScreenHeader atau buat Header khusus untuk Hasil MCU
        PasienScreenHeader( // Atau buat HasilMCUScreenHeader
            title = "Hasil MCU", // Judul baru
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onNavigateBack = onNavigateBack,
            onFilterClicked = { /* TODO: Implement filter logic untuk Hasil MCU */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (val state = uiState) {
                is HasilMCUUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is HasilMCUUiState.Success -> {
                    if (state.paginatedMcuPatients.isEmpty() && searchText.isNotBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Data MCU untuk \"$searchText\" tidak ditemukan.")
                        }
                    } else if (state.paginatedMcuPatients.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada data hasil MCU terdaftar.")
                        }
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.paginatedMcuPatients, key = { it.id }) { mcuPatient -> // Key sekarang McuPatient.id
                                McuListItem( // Composable baru untuk item list
                                    mcuPatient = mcuPatient,
                                    onInfoClicked = {
                                        // Navigasi ke DetailHasilMCUScreen dengan patientId dan patientName dari McuPatient
                                        onNavigateToDetailMCU(
                                            mcuPatient.patientId, // Ini adalah ID pasien global
                                            mcuPatient.name
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is HasilMCUUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchMcuPatientsList() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }

        val currentState = uiState
        if (currentState is HasilMCUUiState.Success && currentState.totalPages > 0) {
            PaginationControls( // Gunakan PaginationControls yang sudah ada
                currentPage = currentState.currentPage,
                totalPages = currentState.totalPages,
                onPageSelected = { page -> viewModel.goToPage(page) },
                onPreviousClicked = { viewModel.previousPage() },
                onNextClicked = { viewModel.nextPage() },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun McuListItem(
    mcuPatient: McuPatient,
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), // Kurangi sedikit padding
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Kurangi elevasi
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) // Warna berbeda
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pasien: ${mcuPatient.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onInfoClicked, modifier = Modifier.size(36.dp)) { // Ukuran tombol info
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Lihat Detail Hasil MCU",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            // HorizontalDivider tidak lagi diperlukan jika Card sudah memberi pemisah visual
            // HorizontalDivider(
            //     color = MaterialTheme.colorScheme.outlineVariant,
            //     modifier = Modifier.padding(vertical = 6.dp)
            // )
            Spacer(modifier = Modifier.height(8.dp))
            HasilMCUDetailRow("ID Pasien", mcuPatient.patientId.toString()) // Menampilkan ID Pasien
            HasilMCUDetailRow("Tgl. Periksa", formatResultDate(mcuPatient.examDate, "dd MMM yyyy"))
            HasilMCUDetailRow("Jenis MCU", mcuPatient.examType)
            HasilMCUDetailRow("Status", mcuPatient.status.name)
        }
    }
}

// Helper Composable untuk baris detail (mirip PatientDetailRow)
@Composable
fun HasilMCUDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top, // Gunakan Top agar label dan value panjang bisa rapi
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f) // Beri weight agar bisa wrap
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f) // Beri weight agar bisa wrap
        )
    }
}

// Anda mungkin ingin membuat PasienScreenHeader lebih generik atau duplikat
// untuk HasilMCUScreenHeader dengan judul yang berbeda
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasienScreenHeader(
    title: String,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onFilterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = 16.dp)) { // Padding atas untuk seluruh header
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
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.weight(1f), // Search bar mengambil sisa ruang
                colors = OutlinedTextFieldDefaults.colors(
                    // ... (warna seperti sebelumnya atau sesuaikan dengan tema M3) ...
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
                placeholder = {
                    Text(
                        text = "Cari...",
                        // style = MaterialTheme.typography.bodyLarge // Sesuaikan style placeholder
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        // tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = onFilterClicked,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), // Sesuaikan border
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(IntrinsicSize.Min) // Agar tinggi tombol filter sama dengan textfield
                    .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp), // Pastikan ukuran minimal
                contentPadding = PaddingValues(0.dp) // Hapus padding default jika ikon saja
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter Button",
                    // tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HasilMCUScreenPreview() {
    OccuHelpTheme {
        HasilMCUScreen(
            onNavigateBack = {},
            onNavigateToDetailMCU = { _, _ -> }
        )
    }
}