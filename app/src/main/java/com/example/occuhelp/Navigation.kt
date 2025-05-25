package com.example.occuhelp

import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object HomePage : Screen("dashboard_screen")
    object AboutUs : Screen("about_us_screen")
    object Pasien : Screen("pasien_screen")
    object Kontak : Screen("kontak_screen")
    object Layanan : Screen("layanan_kami_screen")
    object DetailPasien : Screen("detail_pasien_screen/{patientId}"){
        const val patientIdArg = "patientId"
        fun createRoute(patientId: Int) = "detail_pasien_screen/$patientId"
        val arguments = listOf(
            navArgument(patientIdArg) { type = NavType.IntType }
        )
    }
    object Report : Screen("report_screen")
    object Rekapitulasi : Screen("rekapitulasi_screen")
    object HasilMCU : Screen("hasil_mcu_screen")
    object DetailMCU : Screen("detail_mcu_screen/{patientId}/{patientName}") {
        const val patientIdArg = "patientId"
        const val patientNameArg = "patientName"
        fun createRoute(patientId: Int, patientName: String) = // Jika patientName bisa null, ubah jadi String?
            "detail_mcu_screen/$patientId/${Uri.encode(patientName)}" // Beri fallback jika null
        val arguments = listOf(
            navArgument(patientIdArg) { type = NavType.IntType },
            navArgument(patientNameArg) {
                type = NavType.StringType
                // nullable = true // Tambahkan jika patientName bisa null
                // defaultValue = "Unknown" // Atau berikan default
            }
        )
    }
    object UbahPassword : Screen("ubah_password_screen")
}