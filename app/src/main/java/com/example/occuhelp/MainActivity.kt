package com.example.occuhelp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.occuhelp.ui.OccuHelpTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val loginViewModel: LoginViewModel = viewModel()
                val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                val userSessionViewModel: UserSessionViewModel = viewModel()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        // ModalDrawerSheet adalah container standar untuk konten drawer
                        ModalDrawerSheet {
                            Sidebar(
                                userSessionViewModel = userSessionViewModel,
                                onItemSelected = { route -> // Callback saat item menu diklik
                                    scope.launch {
                                        drawerState.close() // Tutup drawer
                                    }
                                    if (route == Screen.HomePage.route) {
                                        // Jika target adalah Dashboard dan userName ada
                                        navController.navigate(Screen.HomePage.route) {
                                            popUpTo(Screen.HomePage.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }else if (route == Screen.HomePage.route) {
                                        // Kasus aneh: mau ke dashboard tapi tidak ada user? Mungkin logout dan ke login
                                        // Atau tampilkan pesan error. Untuk sekarang, anggap ini tidak terjadi jika alur benar.
                                        // Atau, jika Anda ingin dashboard bisa diakses tanpa nama (misalnya dengan nama default)
                                        // Anda perlu logika tambahan di composable DashboardScreen atau rute tanpa argumen.
                                        navController.navigate(Screen.Login.route) { // Darurat: kembali ke login
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                    else {
                                        // Untuk rute sidebar lainnya
                                        navController.navigate(route) {
                                            popUpTo(Screen.HomePage.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                onCloseDrawer = { // Callback untuk tombol close di sidebar
                                    scope.launch {
                                        drawerState.close()
                                    }
                                },
                                onLogout = {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    // Hapus token, navigasi ke LoginScreen
                                    loginViewModel.performLogoutCleanup() // Jika ada fungsi logout di ViewModel
                                    userSessionViewModel.clearUser()      // <-- CLEAR USER SESSION
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                    Toast.makeText(context, "Anda telah keluar", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                ) {
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
                                            userSessionViewModel.setUser(event.userName)
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
                        composable(Screen.HomePage.route) { navBackStackEntry ->
                            DashboardScreen(
                                userSessionViewModel = userSessionViewModel,
                                onMenuClicked = { // Callback untuk membuka drawer
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
                        }
                        composable(Screen.AboutUs.route){
                            AboutUsScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}