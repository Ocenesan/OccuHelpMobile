package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import androidx.compose.runtime.LaunchedEffect // Untuk event dari ViewModel
import androidx.compose.ui.platform.LocalContext // Untuk Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk OutlinedTextFieldDefaults
@Composable
fun UbahPasswordScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
    onNavigateToPasswordUpdated: () -> Unit,
    viewModel: UbahPasswordViewModel = viewModel()
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    // var oldPasswordVisible by remember { mutableStateOf(false) } // Tidak perlu
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) } // Tambahkan untuk konfirmasi

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle UI Events dari ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UbahPasswordEvent.ShowFeedback -> {
                    Toast.makeText(context, event.message, if (event.isError) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
                }
                is UbahPasswordEvent.NavigateToPasswordUpdated -> {
                    onNavigateToPasswordUpdated() // Panggil callback navigasi ke layar sukses
                }
            }
        }
    }

    Column(
        modifier = modifier // Gunakan modifier yang di-pass
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 32.dp),
    ) {

        IconButton(
            onClick = { if (!isLoading) onBackClicked() },
            modifier = Modifier
                .size(44.dp)
                .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium),
            enabled = !isLoading
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Silahkan Ganti\nKata Sandi Anda!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Identitasmu telah diverifikasi.\nSilahkan masukkan kata sandi yang baru!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Kata Sandi Baru
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = {
                Text(
                    "Kata Sandi Baru",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                    Icon(
                        imageVector = if (newPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (newPasswordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge // textStyle sudah punya warna Black dari AppTypography
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Konfirmasi Kata Sandi Baru
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = {
                Text(
                    "Konfirmasi Kata Sandi Baru",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            enabled = !isLoading,
            trailingIcon = { // Tambahkan visibility toggle untuk konfirmasi juga
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Sembunyikan" else "Tampilkan"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f)) // Mendorong tombol ke bawah

        // Tombol Perbarui Kata Sandi
        OutlinedButton(
            onClick = { viewModel.onUpdatePasswordClicked(newPassword, confirmPassword) },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Atau outline jika beda
            shape = MaterialTheme.shapes.small, // Atau medium jika ingin sama dengan text field
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp) ,
            enabled = !isLoading
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                tint = MaterialTheme.colorScheme.primary, // Sama dengan contentColor
                modifier = Modifier.size(24.dp) // Ukuran ikon yang lebih standar untuk tombol
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Perbarui Kata Sandi",
                style = MaterialTheme.typography.labelMedium // Atau labelLarge jika ingin lebih besar
                // Warna sudah dari contentColor tombol
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UbahPasswordScreenPreview() {
    OccuHelpTheme {
        UbahPasswordScreen(
            onBackClicked = {},
            onNavigateToPasswordUpdated = {}
        )
    }
}