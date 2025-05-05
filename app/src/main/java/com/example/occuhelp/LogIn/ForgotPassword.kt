package com.example.occuhelp.LogIn

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailVerificationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Masukkan Email\nuntuk Verifikasi",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007FA3)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Silahkan ketik email Anda pada kolom di\nbawah ini, agar kami bisa mengirimkan",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF007FA3)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        var email by remember { mutableStateOf("") }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF009ECF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Kirim Link Verifikasi")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailVerificationScreen() {
    EmailVerificationScreen()
}