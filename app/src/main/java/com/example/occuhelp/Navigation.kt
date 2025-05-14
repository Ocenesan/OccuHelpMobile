package com.example.occuhelp

sealed class Screen(val route: String) {
    object Login : Screen("login_screen")
    object ForgotPassword : Screen("forgot_password_screen")
}