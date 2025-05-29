package com.example.occuhelp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.vector.ImageVector

enum class LoginPopUpType(val message: String, val icon: ImageVector) {
    WRONG_CREDENTIALS(
        message = "NIP/Kata sandi yang Anda masukkan salah.",
        icon = Icons.Filled.Close
    ),
    NETWORK_ERROR(
        message = "Koneksi internet bermasalah. Silakan coba lagi.",
        icon = Icons.Filled.WifiOff
    ),
    EMPTY_TEXTFIELD(
        message = "Tidak ada data yang valid. Isi data terlebih dahulu!",
        icon = Icons.Filled.Warning
    ),
    EMAIL_VERIFICATION(
        message = "Tautan verifikasi sudah dikirim melalui email anda.",
        icon = Icons.Filled.Verified
    ),
    EMAIL_FAILED(
        message = "Gagal mengirim link reset. Coba lagi nanti.",
        icon = Icons.Filled.Cancel
    ),
    GANTI_PASSWORD(
        message = "Apakah Anda yakin ingin mengubah kata sandi?",
        icon = Icons.Filled.Person
    ),
    LOG_OUT(
        message = "Apakah Anda yakin ingin keluar dari akun?",
        icon = Icons.AutoMirrored.Filled.Logout
    )
}