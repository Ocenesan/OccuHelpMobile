package com.example.occuhelp.LogIn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KataSandiBerhasilScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kata Sandi\nDiperbarui!",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF007FA3)
            ),
            modifier = Modifier.padding(top = 32.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kata sandi Anda berhasil diperbarui.\nSilahkan Login kembali.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF007FA3)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .border(
                    BorderStroke(6.dp, Color(0xFF007FA3)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF007FA3),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

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
            Text("Masuk")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KataSandiBerhasilPreview() {
    KataSandiBerhasilScreen()
}
