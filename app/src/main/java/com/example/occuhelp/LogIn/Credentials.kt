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
fun GantiKataSandiScreen() {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Silahkan Ganti\nKata Sandi Anda!",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007FA3)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Identitasmu telah diverifikasi.\nSilahkan masukkan kata sandi yang baru!",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF007FA3)
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Kata Sandi Lama") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Kata Sandi Baru") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Konfirmasi Kata Sandi Baru") },
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
            Text("Perbarui Kata Sandi")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GantiKataSandiPreview() {
    GantiKataSandiScreen()
}
