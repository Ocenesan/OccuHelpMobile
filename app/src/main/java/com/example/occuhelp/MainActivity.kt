package com.example.occuhelp

import kotlinx.coroutines.launch
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.occuhelp.ui.OccuHelpTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                val navController = rememberNavController()
                var currentLoginError by remember { mutableStateOf<LoginPopUpType?>(null) }
                var isLoading by remember { mutableStateOf(false) }
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                val showApiFeedback = { message: String, isError: Boolean ->
                    if (isError) {
                        // Jika ingin menggunakan dialog generik untuk semua error API
                        // currentApiError = LoginPopUpType.NETWORK_ERROR // Atau jenis error lain yang sesuai
                        // Atau cukup tampilkan Toast
                        Toast.makeText(context, "Error: $message", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    }
                }

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
                            isLoading = isLoading,
                            onLoginClicked = { nip, password ->
                                currentLoginError = null
                                if (!isNetworkAvailable(context)) {
                                    currentLoginError = LoginPopUpType.NETWORK_ERROR
                                    return@LoginScreen
                                }

                                if (nip.isBlank() || password.isBlank()) {
                                    currentLoginError = LoginPopUpType.EMPTY_TEXTFIELD
                                    return@LoginScreen
                                } // Panggil API Login
                                isLoading = true // Mulai loading
                                scope.launch {
                                    try {
                                        val request = LoginRequest(nip, password)
                                        val response = RetrofitClient.apiService.login(request)

                                        if (response.isSuccessful && response.body() != null) {
                                            val authResponse = response.body()!!
                                            // Login Berhasil
                                            println("Login successful: Token: ${authResponse.token}, User: ${authResponse.user.name}")
                                            Toast.makeText(context, "Login berhasil! Selamat datang ${authResponse.user.name}", Toast.LENGTH_LONG).show()

                                            // TODO: Simpan token (misal di SharedPreferences atau DataStore)
                                            // contoh: saveAuthToken(context, authResponse.token)

                                            // TODO: Navigasi ke halaman utama setelah login
                                            navController.navigate(Screen.HomePage.route) {
                                                popUpTo(Screen.Login.route) { inclusive = true }
                                            }
                                        } else {
                                            // Error dari API (misal NIP/password salah)
                                            // Anda mungkin ingin parse errorBody untuk pesan error yang lebih spesifik dari API
                                            //val errorBody = response.errorBody()?.string()
                                            //println("API Error: $errorBody")
                                            currentLoginError = LoginPopUpType.WRONG_CREDENTIALS
                                            //println("Login API error: ${response.code()} - ${response.message()}")
                                        }
                                    } catch (e: Exception) {
                                        // Error jaringan atau exception lain
                                        currentLoginError = LoginPopUpType.NETWORK_ERROR
                                        println("Login exception: ${e.localizedMessage}")
                                        e.printStackTrace()
                                    } finally {
                                        isLoading = false // Selesai loading
                                    }
                                }
                            },
                            onChangePasswordClicked = {
                                if (!isLoading) { // Hanya navigasi jika tidak sedang loading
                                    navController.navigate(Screen.ForgotPassword.route)
                                }
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
                                if (!isLoading) navController.popBackStack()
                            },
                            isLoading = isLoading,
                            onSendLinkClicked = { email ->
                                currentLoginError = null
                                if (!isNetworkAvailable(context)) {
                                    currentLoginError = LoginPopUpType.NETWORK_ERROR
                                    return@ForgotPasswordScreen
                                }
                                if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                                    currentLoginError = LoginPopUpType.EMPTY_TEXTFIELD
                                    return@ForgotPasswordScreen
                                }
                                isLoading = true
                                scope.launch {
                                    try {
                                        val request = ResetLinkRequest(email)
                                        val response = RetrofitClient.apiService.sendResetLink(request)

                                        if (response.isSuccessful && response.body() != null) {
                                            val baseResponse = response.body()!!
                                            val message = baseResponse.message ?: "Link reset telah dikirim ke email Anda."
                                            showApiFeedback(message, false)
                                            navController.popBackStack() // Kembali ke login setelah sukses
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            var errorMessage = "Gagal mengirim link reset. Coba lagi nanti."
                                            if (!errorBody.isNullOrBlank()) {
                                                try {
                                                    val baseResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                                                    if (!baseResponse.message.isNullOrBlank()) {
                                                        errorMessage = baseResponse.message
                                                    } else if (!baseResponse.error.isNullOrBlank()) {
                                                        errorMessage = baseResponse.error
                                                    }
                                                } catch (e: Exception) {
                                                    println("Failed to parse error body: $errorBody")
                                                }
                                            }
                                            println("Send reset link API error: ${response.code()} - ${response.message()} - Body: $errorBody")
                                            showApiFeedback(errorMessage, true)
                                            // currentApiError = LoginPopUpType.NETWORK_ERROR // atau tipe error yang sesuai
                                        }
                                    } catch (e: Exception) {
                                        showApiFeedback("Terjadi kesalahan: ${e.localizedMessage}", true)
                                        // currentApiError = LoginPopUpType.NETWORK_ERROR
                                        println("Send reset link exception: ${e.localizedMessage}")
                                        e.printStackTrace()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.HomePage.route) {
                        AboutUsScreen(
//                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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