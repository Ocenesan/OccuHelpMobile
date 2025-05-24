package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.CircularProgressIndicator // Untuk loading
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasienScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    pasienViewModel: PasienViewModel = viewModel()
) {
    val uiState by pasienViewModel.uiState.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState() // Untuk scroll ke atas saat halaman berubah

    // Efek untuk memicu pencarian saat searchText berubah
    LaunchedEffect(searchText) {
        // Tambahkan delay kecil (debounce) jika ingin agar tidak mencari setiap ketikan
        // kotlinx.coroutines.delay(300) // Contoh debounce
        pasienViewModel.searchPatients(searchText)
    }

    LaunchedEffect(key1 = (uiState as? PasienUiState.Success)?.currentPage) {
        if (uiState is PasienUiState.Success && (uiState as PasienUiState.Success).paginatedPatients.isNotEmpty()) {
            if (lazyListState.layoutInfo.totalItemsCount > 0) { // Pastikan ada item sebelum scroll
                lazyListState.scrollToItem(0)
            }
        }
    }

    // Menggunakan Column untuk tata letak utama
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // Padding horizontal umum untuk layar
    ) {
        // Bagian Header (Tombol Kembali, Judul, Search, Filter)
        PasienScreenHeader(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onNavigateBack = onNavigateBack,
            onFilterClicked = { /* TODO: Implement filter logic */ }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Jarak antara header dan daftar pasien

        // Daftar Pasien (LazyColumn)
        Box(
            modifier = Modifier.weight(1f) // LazyColumn mengambil sisa ruang
        ) {
            when (val state = uiState) {
                is PasienUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PasienUiState.Success -> {
                    if (state.paginatedPatients.isEmpty() && searchText.isNotBlank()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Pasien \"$searchText\" tidak ditemukan.")
                        }
                    } else if (state.paginatedPatients.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada data pasien.")
                        }
                    } else {
                        LazyColumn(
                            state = lazyListState,
                            // Tambahkan padding vertikal untuk item agar tidak terlalu mepet
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(state.paginatedPatients, key = { it.id }) { patient ->
                                PatientListItem(
                                    patient = patient,
                                    onInfoClicked = {
                                        println("Info clicked for patient ID: ${patient.id}")
                                        // TODO: Navigasi ke DetailPasienScreen(patient.id)
                                    }
                                )
                            }
                        }
                    }
                }
                is PasienUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pasienViewModel.fetchPatients() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }

        // Pagination UI
        val currentState = uiState
        if (currentState is PasienUiState.Success && currentState.totalPages > 0) {
            PaginationControls(
                currentPage = currentState.currentPage,
                totalPages = currentState.totalPages,
                onPageSelected = { page -> pasienViewModel.goToPage(page) },
                onPreviousClicked = { pasienViewModel.previousPage() },
                onNextClicked = { pasienViewModel.nextPage() },
                modifier = Modifier.padding(vertical = 4.dp) // Padding untuk pagination
            )
        } else {
            // Spacer jika tidak ada pagination agar layout konsisten
            Spacer(modifier = Modifier.height(48.dp)) // Sesuaikan tinggi
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasienScreenHeader(
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
                text = "Pasien",
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

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    onPreviousClicked: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier,
    maxPageButtonsToShow: Int = 3 // Jumlah maksimal tombol angka halaman yang ditampilkan
) {
    Row(
        modifier = modifier.fillMaxWidth(), // Memastikan Row mengambil lebar penuh
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Pusatkan kontrol pagination
    ) {
        TextButton(
            onClick = { onPreviousClicked() },
            enabled = currentPage > 1
        ) {
            Text(
                text = "Sebelumnya",
                style = MaterialTheme.typography.displayMedium, // Sesuaikan style
                color = if (currentPage > 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Logika untuk menampilkan tombol angka halaman secara dinamis
        val pageRange = remember(totalPages, currentPage, maxPageButtonsToShow) {
            getPageRange(currentPage, totalPages, maxPageButtonsToShow)
        }

        // Tombol "..." jika halaman pertama tidak terlihat
        if (pageRange.first > 1) {
            Text("...", modifier = Modifier.padding(horizontal = 4.dp).align(Alignment.CenterVertically))
        }

        pageRange.forEach { page ->
            TextButton(
                onClick = { onPageSelected(page) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (page == currentPage) MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent,
                    contentColor = if (page == currentPage) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.sizeIn(minWidth = 40.dp, minHeight = 40.dp), // Ukuran tombol angka
                shape = CircleShape // Bentuk lingkaran
            ) {
                Text(text = "$page", fontWeight = if (page == currentPage) FontWeight.Bold else FontWeight.Normal)
            }
        }

        // Tombol "..." jika halaman terakhir tidak terlihat
        if (pageRange.last < totalPages) {
            Text("...", modifier = Modifier.align(Alignment.CenterVertically))
        }


        Spacer(modifier = Modifier.width(8.dp))

        TextButton(
            onClick = { onNextClicked() },
            enabled = currentPage < totalPages
        ) {
            Text(
                text = "Selanjutnya",
                style = MaterialTheme.typography.displayMedium, // Sesuaikan style
                color = if (currentPage < totalPages) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Fungsi helper untuk menentukan range halaman yang akan ditampilkan
fun getPageRange(currentPage: Int, totalPages: Int, maxButtons: Int): IntRange {
    if (totalPages <= maxButtons) {
        return 1..totalPages
    }

    val halfButtons = maxButtons / 2
    var startPage = max(1, currentPage - halfButtons)
    var endPage = min(totalPages, currentPage + halfButtons - (if (maxButtons % 2 == 0) 1 else 0))

    // Adjust if we're near the beginning
    if (currentPage - halfButtons < 1) {
        endPage = maxButtons
    }

    // Adjust if we're near the end
    if (currentPage + halfButtons > totalPages) {
        startPage = totalPages - maxButtons + 1
    }
    // Pastikan startPage tidak negatif atau nol jika totalPages sangat kecil
    startPage = max(1, startPage)
    endPage = min(totalPages, endPage)

    return startPage..endPage
}

@Composable
fun PatientListItem(
    patient: Patient,
    onInfoClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${patient.medRecordId}",
                    style = MaterialTheme.typography.bodySmall, // Sesuaikan style
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(
                    onClick = { onInfoClicked() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Button",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant, // Warna yang lebih soft
                thickness = 1.dp,
                modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
            Text(
                text = patient.name,
                style = MaterialTheme.typography.displayMedium,
                modifier = modifier.padding(vertical = 10.dp)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tgl. Periksa",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = patient.examDate.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Unit Kerja",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = patient.unit ?: "-",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Jenis Pemeriksaan",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "Medical Check Up",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasienScreenPreview() {
    OccuHelpTheme {
        PasienScreen(onNavigateBack = {})
    }
}