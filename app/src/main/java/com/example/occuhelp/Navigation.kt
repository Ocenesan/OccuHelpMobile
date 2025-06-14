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
    object DetailMCU : Screen("detail_mcu_screen/{patientId}/{patientName}") { // Rute untuk detail spesifik satu hasil MCU
        const val patientIdArg = "patientId"
        const val patientNameArg = "patientName"
        // Jika DetailHasilMCUScreen menerima mcuResultId, bukan patientId:
        // const val mcuResultIdArg = "mcuResultId"
        // fun createRoute(mcuResultId: Int, patientId: Int, patientName: String) = "detail_mcu_screen/$mcuResultId/$patientId/${Uri.encode(patientName)}"
        fun createRoute(patientId: Int, patientName: String) = // Jika masih menggunakan patientId & patientName
            "detail_mcu_screen/$patientId/${Uri.encode(patientName)}"
        val arguments = listOf(
            navArgument(patientIdArg) { type = NavType.IntType },
            navArgument(patientNameArg) { type = NavType.StringType }
            // navArgument(mcuResultIdArg) { type = NavType.IntType }, // Jika pakai mcuResultId
        )
    }
    object UbahPassword : Screen("ubah_password_screen/{token}/{email}") { // Terima token & email
        const val tokenArg = "token"
        const val emailArg = "email"
        fun createRoute(token: String, email: String) =
            "ubah_password_screen/${Uri.encode(token)}/${Uri.encode(email)}"
        val arguments = listOf(
            navArgument(tokenArg) { type = NavType.StringType },
            navArgument(emailArg) { type = NavType.StringType }
        )
    }
    object PasswordUpdated : Screen("password_updated_screen")
}