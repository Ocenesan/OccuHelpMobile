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
import android.content.Intent // Untuk onNewIntent
import android.net.Uri

class MainActivity : ComponentActivity() {
    private var deepLinkToken: String? = null
    private var deepLinkEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            OccuHelpTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val loginViewModel: LoginViewModel = viewModel()
                val userSessionViewModel: UserSessionViewModel = viewModel()
                val showChangePasswordDialog by loginViewModel.showChangePasswordConfirmationDialog.collectAsStateWithLifecycle()

                if (showChangePasswordDialog) {
                    ConfirmationDialog(
                        confirmationType = LoginPopUpType.GANTI_PASSWORD,
                        confirmButtonText = "Ya",
                        dismissButtonText = "Batal",
                        onDismissRequest = { loginViewModel.onDismissChangePasswordConfirmation() },
                        onConfirm = {
                            loginViewModel.onConfirmChangePassword()
                        },
                        onDismiss = { loginViewModel.onDismissChangePasswordConfirmation() }
                    )
                }

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
                        }
                    }
                }

                // Navigasi awal berdasarkan deep link jika ada
                LaunchedEffect(deepLinkToken, deepLinkEmail) {
                    if (deepLinkToken != null && deepLinkEmail != null) {
                        navController.navigate(
                            Screen.UbahPassword.createRoute(deepLinkToken!!, deepLinkEmail!!)
                        ) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                        this@MainActivity.deepLinkToken = null
                        this@MainActivity.deepLinkEmail = null
                    }
                }

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
                                    } else {
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
                            val isLoading by loginViewModel.isLoading.collectAsStateWithLifecycle()
                            val loginError by loginViewModel.loginError.collectAsStateWithLifecycle()

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
                            // val apiError by forgotPasswordViewModel.apiError.collectAsStateWithLifecycle() // Jika pakai state error

                            /*LaunchedEffect(Unit) {
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
                            }*/
                            ForgotPasswordScreen(
                                onBackClicked = {
                                    navController.popBackStack()
                                },
                                onNavigateToUbahPassword = { token, email ->
                                    navController.navigate(Screen.UbahPassword.createRoute(token, email)) {
                                        // Opsi: popUpTo ForgotPasswordScreen agar tidak kembali ke sana
                                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(
                            route = Screen.UbahPassword.route, // Ini adalah routeWithArgs
                            arguments = Screen.UbahPassword.arguments
                        ) {
                            UbahPasswordScreen(
                                onBackClicked = { navController.popBackStack(Screen.Login.route, inclusive = false) },
                                // Tambahkan callback baru untuk navigasi setelah password berhasil direset
                                onNavigateToPasswordUpdated = { // Implementasi callback
                                    navController.navigate(Screen.PasswordUpdated.route) {
                                        // Hapus UbahPasswordScreen dari back stack
                                        popUpTo(Screen.UbahPassword.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                        composable(Screen.PasswordUpdated.route) {
                            PasswordUpdatedScreen(
                                onLoginClicked = {
                                    navController.navigate(Screen.Login.route) {
                                        // Bersihkan seluruh back stack dan buat LoginScreen sebagai root baru
                                        // Ini memastikan pengguna tidak bisa kembali ke PasswordUpdatedScreen
                                        // atau layar sebelumnya dalam alur reset password.
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
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
                        composable(Screen.Pasien.route) {
                            PasienScreen(onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetail = { patientId ->
                                    navController.navigate(Screen.DetailPasien.createRoute(patientId))
                                }
                            )
                        }
                        composable(Screen.Kontak.route) {
                            KontakScreen(onNavigateBack = { navController.popBackStack() })
                        }
                        composable(Screen.Layanan.route) {
                            LayananKamiScreen(onNavigateBack = { navController.popBackStack() })
                        }
                        composable(Screen.Report.route) {
                            ReportScreen(onNavigateBack = { navController.popBackStack() })
                        }
                        composable(Screen.HasilMCU.route) {
                            HasilMCUScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToDetailMCU = { patientId, patientName ->
                                    navController.navigate(Screen.DetailMCU.createRoute(patientId, patientName ?: "Detail Pasien"))
                                }
                            )
                        }
                        composable(Screen.Rekapitulasi.route) {
                            RekapitulasiScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(
                            route = Screen.DetailPasien.route,
                            arguments = Screen.DetailPasien.arguments
                        ) {
                            DetailPasienScreen(
                                onNavigateBack = { navController.popBackStack() },
                                // Tambahkan callback untuk navigasi ke Detail MCU
                                onNavigateToMcuResults = { patientId, patientName ->
                                    navController.navigate(Screen.DetailMCU.createRoute(patientId, patientName))
                                }
                            )
                        }
                        composable(
                            Screen.DetailMCU.route,
                            arguments = Screen.DetailMCU.arguments
                        ) { backStackEntry ->
                            DetailHasilMCUScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
    // ---> UBAH PARAMETER intent MENJADI Intent (NON-NULLABLE) <---
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent) // Panggil super dengan intent non-nullable
        // Tidak perlu 'intent?.let' lagi karena intent dijamin non-null di sini
        handleIntent(intent) // Tangani intent jika Activity sudah berjalan
    }

    // Parameter intent di sini juga sebaiknya non-nullable jika dipanggil dari onCreate dan onNewIntent
    private fun handleIntent(intent: Intent) {
        val action: String? = intent.action
        val data: Uri? = intent.data

        if (Intent.ACTION_VIEW == action && data != null) {
            if (data.scheme == "occuhelp" && data.host == "reset-password") {
                val token = data.getQueryParameter("token")
                val email = data.getQueryParameter("email")

                if (!token.isNullOrBlank() && !email.isNullOrBlank()) {
                    this.deepLinkToken = token
                    this.deepLinkEmail = email
                    android.util.Log.d("DEEP_LINK", "Token: $token, Email: $email")
                }
            }
        }
    }
}