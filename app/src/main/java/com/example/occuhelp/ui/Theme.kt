package com.example.occuhelp.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.occuhelp.R

// --- Definisi Warna yang Akan Digunakan di ColorScheme ---
val OccuHelpPrimary = Color(0xFF3393AD)
val OccuHelpSecondary = Color(0xFF299BB8)
val OccuHelpBackground = Color.White
val OccuHelpSurface = Color(0xFF185C6D)
val OccuHelpOnPrimary = Color.White
val OccuHelpOnSecondary = Color.White
val OccuHelpOnBackground = Color.Black
val OccuHelpOnSurface = Color.Black
val OccuHelpOutline = Color(0xFF009FE3)
val OccuHelpBodyText = Color(0xFF299BB8)
val OccuHelpSmallText = Color.Gray
val OccuHelpInputText = Color.Black

// Warna spesifik UI yang mungkin tidak masuk ColorScheme secara langsung (gunakan dengan hati-hati)
val OccuHelpBackButtonBackground = Color(0xFFC1D6E6) // GrayishBlue
val OccuHelpBackButtonIcon = Color(0xFF185C6D)     // DarkBlueGray

// --- Definisi Font Family ---
val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

val OpenSans = FontFamily(
    Font(R.font.opensans_regular, FontWeight.Normal),
    Font(R.font.opensans_medium, FontWeight.Medium),
    Font(R.font.opensans_semibold, FontWeight.SemiBold)
)

// --- Definisi Tipografi ---
// Sekarang, AppTypography akan mengambil warna default dari ColorScheme jika tidak dispesifikkan di sini
val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 38.sp
        // Warna akan diambil dari colorScheme.onBackground atau colorScheme.primary
    ),
    headlineMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleMedium = TextStyle( // Untuk Label di TextField
        fontFamily = OpenSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
        // Warna akan diambil dari colorScheme.primary atau colorScheme.onSurface saat digunakan sebagai label
    ),
    bodyLarge = TextStyle( // Teks Input di TextField
        fontFamily = OpenSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = OccuHelpInputText // Bisa juga colorScheme.onSurface
    ),
    bodyMedium = TextStyle( // Teks Body utama
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = OccuHelpBodyText // Warna spesifik untuk body text ini
    ),
    bodySmall = TextStyle( // Teks Kecil
        fontFamily = OpenSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        color = OccuHelpSmallText
    ),
    labelLarge = TextStyle( // Teks untuk Tombol Besar
        fontFamily = OpenSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
        // Warna akan diambil dari colorScheme.primary atau contentColor tombol
    ),
    labelMedium = TextStyle( // Teks untuk Tombol Sedang
        fontFamily = OpenSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp
    )
)

// --- Definisi Bentuk (Shapes) ---
val AppShapes = Shapes(
    small = RoundedCornerShape(7.dp), // Untuk tombol "Kirim Link Verifikasi"
    medium = RoundedCornerShape(12.dp), // Untuk TextField, Tombol Kembali
    large = RoundedCornerShape(16.dp) // Contoh lain
)

// --- Definisi Skema Warna ---
private val LightColorScheme = lightColorScheme(
    primary = OccuHelpPrimary,
    onPrimary = OccuHelpOnPrimary,
    secondary = OccuHelpSecondary,
    onSecondary = OccuHelpOnSecondary,
    background = OccuHelpBackground,
    onBackground = OccuHelpOnBackground,
    surface = OccuHelpSurface,
    onSurface = OccuHelpOnSurface,
    outline = OccuHelpOutline,
    // Anda bisa menambahkan warna lain seperti error, inversePrimary, dll.
)

// Opsional: Skema Warna Gelap (Anda bisa biarkan sama dengan Light jika belum butuh)
private val DarkColorScheme = darkColorScheme(
    primary = OccuHelpPrimary,
    onPrimary = OccuHelpOnPrimary, // Mungkin perlu diubah untuk kontras di dark mode
    secondary = OccuHelpSecondary,
    onSecondary = OccuHelpOnSecondary,
    background = Color(0xFF121212), // Contoh background gelap
    onBackground = Color(0xFFE0E0E0), // Contoh teks terang di background gelap
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    outline = OccuHelpOutline
)

// --- Composable Tema Utama ---
@Composable
fun OccuHelpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme // Aktifkan jika Anda mau handle dark theme yang berbeda
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb() // Atau Color.Transparent jika desainnya begitu
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme // True untuk status bar terang (ikon gelap)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}