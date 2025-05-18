package com.example.occuhelp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.occuhelp.ui.OccuHelpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                val navController = rememberNavController()
                var currentLoginError by remember { mutableStateOf<LoginPopUpType?>(null) }
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ){
                    composable(Screen.Login.route) {
                        LoginScreen(
                            currentLoginError = currentLoginError,
                            onDismissErrorDialog = {
                                currentLoginError = null // Clear the error when dialog is dismissed
                            },
                            onLoginClicked = { nip, password ->
                                // Clear previous error before attempting login
                                currentLoginError = null

                                // 1. Cek Koneksi Internet Terlebih Dahulu
                                if (!isNetworkAvailable(context)) {
                                    currentLoginError = LoginPopUpType.NETWORK_ERROR
                                    return@LoginScreen // Keluar lebih awal jika tidak ada internet
                                }

                                if (nip.isBlank() || password.isBlank()) {
                                    currentLoginError = LoginPopUpType.EMPTY_TEXTFIELD
                                } else {
                                    currentLoginError = LoginPopUpType.WRONG_CREDENTIALS
                                    println("Login attempt failed for NIP: $nip, Password: $password")
                                }
                            },
                            onChangePasswordClicked = {
                                navController.navigate(Screen.ForgotPassword.route)
                            }
                        )
                    }
                    composable(Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(
                            currentLoginError = currentLoginError,
                            onDismissErrorDialog = {
                                currentLoginError = null // Clear the error when dialog is dismissed
                            },
                            onBackClicked = {
                                // Aksi untuk kembali ke layar sebelumnya (LoginScreen)
                                navController.popBackStack()
                            },
                            onSendLinkClicked = { email ->
                                currentLoginError = null
                                if (!isNetworkAvailable(context)) {
                                    currentLoginError = LoginPopUpType.NETWORK_ERROR
                                    return@ForgotPasswordScreen // Keluar lebih awal jika tidak ada internet
                                }
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // API 23 (Marshmallow) ke atas
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
                else -> false
            }
        } else { // API di bawah 23
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }
}