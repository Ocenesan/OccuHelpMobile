package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.occuhelp.ui.OccuHelpTheme

@Composable
private fun BaseDialogLayout(
    icon: ImageVector,
    iconContentDescription: String,
    message: String,
    buttons: @Composable RowScope.() -> Unit // Slot untuk tombol-tombol
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A3B4C) // Dark blue-gray background
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(2.dp, Color.White), CircleShape)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = iconContentDescription,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Baris untuk tombol-tombol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly, // Atau Arrangement.End jika mau tombol di kanan
                verticalAlignment = Alignment.CenterVertically
            ) {
                buttons() // Render tombol-tombol yang diteruskan
            }
        }
    }
}

@Composable
fun GenericErrorDialog(
    errorType: LoginPopUpType,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    confirmButtonText: String = "Coba Lagi"
) {
    Dialog(onDismissRequest = onDismissRequest) {
        BaseDialogLayout(
            icon = errorType.icon,
            iconContentDescription = "Error Icon",
            message = errorType.message
        ) {
            // Tombol tunggal untuk GenericErrorDialog
            Button(
                onClick = onConfirmClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3A506B), // Warna tombol sedikit lebih terang
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                modifier = Modifier
                    .weight(1f) // Ambil lebar yang tersedia jika hanya satu tombol
                    .padding(horizontal = 8.dp) // Padding antar tombol jika ada lebih dari satu
                    .height(48.dp)
            ) {
                Text(confirmButtonText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    icon: ImageVector,
    message: String,
    confirmButtonText: String = "Ya",
    dismissButtonText: String = "Tidak",
    iconContentDescription: String = "Confirmation Icon",
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        BaseDialogLayout(
            icon = icon,
            iconContentDescription = iconContentDescription,
            message = message
        ) {
            // Tombol "Tidak" (Dismiss)
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onDismissRequest()
                },
                // ... (styling tombol Tidak seperti sebelumnya) ...
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(48.dp)
            ) {
                Text(dismissButtonText, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Tombol "Ya" (Confirm)
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                // ... (styling tombol Ya seperti sebelumnya) ...
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(48.dp)
            ) {
                Text(confirmButtonText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// --- ConfirmationDialog Overload 2: Menggunakan LoginPopUpType ---
@Composable
fun ConfirmationDialog(
    confirmationType: LoginPopUpType, // Menggunakan LoginPopUpType untuk ikon dan pesan default
    confirmButtonText: String = "Ya",
    dismissButtonText: String = "Batal",
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    // Anda bisa menambahkan parameter message & icon opsional di sini jika ingin override dari LoginPopUpType
    // overrideMessage: String? = null,
    // overrideIcon: ImageVector? = null
) {
    // Gunakan ikon dan pesan dari confirmationType, atau override jika disediakan
    // val iconToShow = overrideIcon ?: confirmationType.icon
    // val messageToShow = overrideMessage ?: confirmationType.message
    // Untuk sekarang, kita langsung gunakan dari confirmationType

    ConfirmationDialog( // Panggil overload pertama
        icon = confirmationType.icon,
        message = confirmationType.message,
        confirmButtonText = confirmButtonText,
        dismissButtonText = dismissButtonText,
        iconContentDescription = "Confirmation Icon based on type", // Bisa lebih spesifik jika perlu
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}


@Preview(showBackground = false)
@Composable
fun PreviewGenericErrorDialogWrongCredentials() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            GenericErrorDialog(
                errorType = LoginPopUpType.WRONG_CREDENTIALS,
                onDismissRequest = {},
                onConfirmClick = {},
                confirmButtonText = "Coba lagi"
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewGenericErrorDialogEmailVerification() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            GenericErrorDialog(
                errorType = LoginPopUpType.EMAIL_VERIFICATION,
                onDismissRequest = {},
                onConfirmClick = {},
                confirmButtonText = "Cek Email" // Example of different button text
            )
        }
    }
}

@Preview(showBackground = false, name = "Confirmation Dialog (From LoginPopUpType)")
@Composable
fun PreviewConfirmationDialogFromType() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            ConfirmationDialog(
                confirmationType = LoginPopUpType.LOG_OUT,
                confirmButtonText = "Ya",
                dismissButtonText = "Batal",
                onDismissRequest = { println("Dialog dismissed (luar/kembali)") },
                onConfirm = { println("Aksi disetujui") },
                onDismiss = { println("Aksi ditolak") }
            )
        }
    }
}