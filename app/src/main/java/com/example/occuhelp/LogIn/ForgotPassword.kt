package com.example.occuhelp.LogIn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ForgotPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmailVerificationScreen()
        }
    }
}

@Composable
fun EmailVerificationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        IconButton(
            onClick = { /* TODO: Back action */ },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF305879),
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFC1D6E6), shape = CircleShape)
                    .padding(4.dp)
            )
        }

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
            label = { Text("Email", color = Color(0xFF007FA3), fontWeight = FontWeight.Medium) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF009FE3),
                unfocusedBorderColor = Color(0xFF009FE3),
                cursorColor = Color(0xFF009FE3),
                focusedLabelColor = Color(0xFF009FE3)
            )

        )

        Spacer(modifier = Modifier.height(450.dp))

        OutlinedButton(
            onClick = { /* TODO: Handle login */ },
            border = BorderStroke(1.dp, Color(0xFF009FE3)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF009FE3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Icon",
                tint = Color(0xFF009FE3)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Kirim Link Verifikasi",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color(0xFF009FE3))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmailVerificationScreen() {
    EmailVerificationScreen()
}