package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBackClicked: () -> Unit = {},
    onNavigateToUbahPassword: (token: String, email: String) -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel(),
    /*onSendLinkClicked: (String) -> Unit,
    currentLoginError: LoginPopUpType?,
    onDismissErrorDialog: () -> Unit,
    isLoading: Boolean*/
) {
    var email by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val context = LocalContext.current // Untuk Intent membuka email

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ForgotPasswordEvent.ShowFeedback -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ForgotPasswordEvent.NavigateToResetPassword -> {
                    // Panggil callback yang akan ditangani MainActivity
                    onNavigateToUbahPassword(event.token, event.email)
                }
            }
        }
    }

    // Menampilkan dialog berdasarkan dialogState
    dialogState?.let { type ->
        // Jika ini adalah dialog verifikasi email, tombolnya berbeda
        if (type == LoginPopUpType.EMAIL_VERIFICATION) {
            GenericErrorDialog( // Kita tetap pakai GenericErrorDialog tapi kustomisasi tombolnya
                errorType = type,
                onDismissRequest = {
                    viewModel.dismissDialog()
                    // Pertimbangkan untuk navigasi kembali ke login setelah dialog ini ditutup
                    // viewModel.triggerNavigateBackEvent() atau langsung panggil onBackClicked()
                    onBackClicked() // Contoh: kembali ke login setelah dialog ditutup
                },
                onConfirmClick = {
                    // Aksi untuk membuka aplikasi email
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Penting jika dipanggil dari konteks non-Activity
                    try {
                        context.startActivity(intent)
                    } catch (e: android.content.ActivityNotFoundException) {
                        // Handle jika tidak ada aplikasi email terinstal
                        // Bisa juga membuka Gmail di browser:
                        val browserIntent = Intent(Intent.ACTION_VIEW,
                            "https://mail.google.com/".toUri())
                        try {
                            context.startActivity(browserIntent)
                        } catch (e2: Exception) {
                            Toast.makeText(context, "Tidak dapat membuka aplikasi email.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.dismissDialog() // Tutup dialog setelah mencoba membuka email
                    onBackClicked() // Contoh: kembali ke login setelah dialog ditutup
                },
                confirmButtonText = "Cek Email" // Teks tombol khusus
            )
        } else {
            // Untuk dialog error lainnya (NETWORK_ERROR, EMPTY_TEXTFIELD dari ViewModel)
            GenericErrorDialog(
                errorType = type,
                onDismissRequest = { viewModel.dismissDialog() },
                onConfirmClick = { viewModel.dismissDialog() } // Tombol default "Coba Lagi" hanya menutup dialog
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 32.dp),
    ) {

        IconButton(
            onClick = { onBackClicked() },
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
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Lupa Kata Sandi?", // Updated text to match "Forgot Password"
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Masukkan email Anda untuk menerima tautan pengaturan ulang kata sandi.", // Updated text
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface // Use onSurface for text on background
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    // style = MaterialTheme.typography.titleMedium, // Already applied by default to label
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { viewModel.onSendLinkClicked(email, context) },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading && email.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_verified_24), // Ensure this drawable exists
                    contentDescription = "Verifikasi icon",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = "Kirim Link Verifikasi",
                    style = MaterialTheme.typography.labelLarge, // Made consistent with LoginScreen button
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    OccuHelpTheme {
        ForgotPasswordScreen(
            onBackClicked = {},
            onNavigateToUbahPassword = { _, _ -> }
        )
    }
}