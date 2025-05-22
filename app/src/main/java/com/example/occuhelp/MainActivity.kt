package com.example.occuhelp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.occuhelp.ui.OccuHelpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ){
                    composable(Screen.Login.route) {
                        // Instansiasi LoginViewModel
                        val loginViewModel: LoginViewModel = viewModel()
                        // Observasi state dari ViewModel
                        val isLoading by loginViewModel.isLoading.collectAsStateWithLifecycle()
                        val loginError by loginViewModel.loginError.collectAsStateWithLifecycle()

                        // Mengobservasi event navigasi dari ViewModel
                        LaunchedEffect(Unit) { // Key bisa Unit jika hanya ingin diluncurkan sekali
                            loginViewModel.loginEvent.collect { event ->
                                when (event) {
                                    is LoginEvent.NavigateToHome -> {
                                        Toast.makeText(context, "Login berhasil! Selamat datang ${event.userName}", Toast.LENGTH_LONG).show()
                                        navController.navigate(Screen.HomePage.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                    is LoginEvent.NavigateToForgotPassword -> {
                                        navController.navigate(Screen.ForgotPassword.route)
                                    }
                                    // Tambahkan penanganan event lain jika ada
                                }
                            }
                        }
                        LoginScreen(
                            currentLoginError = loginError,
                            onDismissErrorDialog = { loginViewModel.dismissErrorDialog() },
                            isLoading = isLoading,
                            onLoginClicked = { nip, password ->
                                loginViewModel.onLoginClicked(nip, password, context)
                            },
                            onChangePasswordClicked = {
                                if (!isLoading) { // Cek loading state dari ViewModel
                                    loginViewModel.onForgotPasswordClicked()
                                }
                            }
                        )
                    }
                    composable(Screen.ForgotPassword.route) {
                        val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                        val isLoading by forgotPasswordViewModel.isLoading.collectAsStateWithLifecycle()
                        // val apiError by forgotPasswordViewModel.apiError.collectAsStateWithLifecycle() // Jika pakai state error

                        LaunchedEffect(Unit) {
                            forgotPasswordViewModel.eventFlow.collect { event ->
                                when (event) {
                                    is ForgotPasswordEvent.ShowFeedback -> {
                                        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                                    }
                                    is ForgotPasswordEvent.NavigateBackToLogin -> {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                        ForgotPasswordScreen(
                            // currentLoginError = apiError, // Jika pakai state error
                            // onDismissErrorDialog = { forgotPasswordViewModel.dismissApiErrorDialog() }, // Jika pakai state error
                            // Sebagai alternatif, ForgotPasswordScreen bisa punya dialog error sendiri atau hanya Toast
                            currentLoginError = null, // Placeholder jika tidak menggunakan dialog error dari state ViewModel ini
                            onDismissErrorDialog = {}, // Placeholder
                            onBackClicked = {
                                if (!isLoading) navController.popBackStack()
                            },
                            isLoading = isLoading,
                            onSendLinkClicked = { email ->
                                forgotPasswordViewModel.onSendLinkClicked(email, context)
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
}