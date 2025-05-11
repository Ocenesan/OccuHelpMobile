package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.R
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon

class ForgotPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme { // Bungkus konten dengan tema Anda
                EmailVerificationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk OutlinedTextFieldDefaults
@Composable
fun EmailVerificationScreen(onBackClicked: () -> Unit = {}) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Gunakan warna background dari tema
            .padding(start = 32.dp, end = 32.dp, top = 40.dp, bottom = 32.dp),
    ) {

        IconButton(
            onClick = { onBackClicked() },
            modifier = Modifier
                .size(44.dp)
                .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon, // Gunakan warna UI spesifik
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Masukkan Email\nuntuk Verifikasi.",
            style = MaterialTheme.typography.headlineLarge, // Harusnya mengambil warna dari ColorScheme
            color = MaterialTheme.colorScheme.secondary // Atau warna spesifik jika berbeda dari default headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Silahkan ketik email Anda pada kolom di\nbawah ini, agar kami bisa mengirimkan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    style = MaterialTheme.typography.titleMedium, // titleMedium sudah diatur font & size nya
                    color = MaterialTheme.colorScheme.primary // Warna label saat fokus/tidak
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium, // Gunakan shape dari tema
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors( // Gunakan warna dari tema
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { /* TODO: Handle login */ },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_verified_24),
                contentDescription = "Verifikasi icon",
                modifier = Modifier
                    .size(28.dp) // Sesuaikan ukuran ikon
                    .padding(end = 8.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Text(
                text = "Kirim Link Verifikasi",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailVerificationScreen() {
    OccuHelpTheme { // Bungkus preview juga dengan tema
        EmailVerificationScreen(onBackClicked = {})
    }
}