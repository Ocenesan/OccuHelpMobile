package com.example.occuhelp

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
        const val patientIdArg = "patientId" // Nama argumen
        fun createRoute(patientId: Int) = "detail_pasien_screen/$patientId"
        val arguments = listOf(
            navArgument(patientIdArg) { type = NavType.IntType }
        )
    }
    object Report : Screen("report_screen")
    object Rekapitulasi : Screen("rekapitulasi_screen")
    object HasilMCU : Screen("hasil_mcu_screen")
    object UbahPassword : Screen("ubah_password_screen")
}