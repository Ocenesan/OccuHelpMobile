package com.example.occuhelp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class LoginPopUpType(val message: String, val icon: ImageVector) {
    WRONG_CREDENTIALS(
        message = "NIP/Kata sandi yang Anda masukkan salah.",
        icon = Icons.Filled.Close // Or your original Icons.Filled.Close
    ),
    NETWORK_ERROR(
        message = "Koneksi internet bermasalah. Silakan coba lagi.",
        icon = Icons.Filled.WifiOff
    ),
    EMPTY_TEXTFIELD(
        message = "Tidak ada data yang valid. Isi data terlebih dahulu!",
        icon = Icons.Filled.Warning
    )
}