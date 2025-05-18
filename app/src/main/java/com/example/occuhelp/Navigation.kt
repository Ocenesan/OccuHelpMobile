package com.example.occuhelp

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object HomePage : Screen("about_us_screen")
    object Pasien : Screen("pasien_screen")
    object DetailPasien : Screen("detail_pasien_screen")
    object Report : Screen("report_screen")
    object HasilMCU : Screen("hasil_mcu_screen")
    object UbahPassword : Screen("ubah_password_screen")
}