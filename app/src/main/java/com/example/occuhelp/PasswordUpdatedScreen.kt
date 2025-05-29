package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.ui.OccuHelpSecondary

@Composable
fun PasswordUpdatedScreen(
    onLoginClicked: () -> Unit = {}  // Callback untuk tombol Masuk
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Gunakan warna background dari tema
            .padding(horizontal = 32.dp, vertical = 40.dp), // Sesuaikan padding
        horizontalAlignment = Alignment.CenterHorizontally // Tengahkan konten kolom secara horizontal
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Kata Sandi\nDiperbarui!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary, // Warna dari tema
            textAlign = TextAlign.Center, // Tengahkan teks judul
            modifier = Modifier.fillMaxWidth() // Agar textAlign.Center berfungsi
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Kata sandi Anda berhasil diperbarui.\nSilahkan Login kembali.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface,// Warna sudah diatur di AppTypography
            textAlign = TextAlign.Center, // Tengahkan teks sub-judul
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            contentAlignment = Alignment.Center, // Ini sudah menengahkan Icon di dalam Box
            modifier = Modifier
                .size(150.dp)
                // Background Box adalah putih, border menggunakan warna secondary dari tema
                .background(MaterialTheme.colorScheme.background, shape = CircleShape) // surface biasanya putih di light theme
                .border(8.dp, OccuHelpSecondary, CircleShape) // Gunakan warna dari tema
        ) {
            Icon(
                imageVector = Icons.Filled.Check, // Menggunakan Icons.Filled.Check
                contentDescription = "Berhasil",
                tint = OccuHelpSecondary, // Warna ikon sama dengan border lingkaran
                modifier = Modifier.size(80.dp) // Ukuran ikon centang lebih besar agar proporsional
            )
        }

        // Spacer ini akan mendorong tombol "Masuk" ke bagian bawah
        Spacer(modifier = Modifier.weight(1f))

        // Tombol Masuk
        OutlinedButton(
            onClick = { onLoginClicked() },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Atau outline
            shape = MaterialTheme.shapes.small, // Atau medium, sesuaikan dengan desain
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_login_24),
                contentDescription = "Masuk Icon",
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordUpdatedScreenPreview() {
    OccuHelpTheme { // Bungkus Preview juga dengan tema
        PasswordUpdatedScreen(onLoginClicked = {})
    }
}