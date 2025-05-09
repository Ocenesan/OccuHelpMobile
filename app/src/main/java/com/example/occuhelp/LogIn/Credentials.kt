package com.example.occuhelp.LogIn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

class Credentials : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GantiKataSandi()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun GantiKataSandi(modifier: Modifier = Modifier) {

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {

        var oldPassword by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {

            IconButton(
                onClick = { /* TODO: Back action */ },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF305879),
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFC1D6E6), shape = CircleShape)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Silahkan Ganti\nKata Sandi Anda!",
                fontSize = 30.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF007FA3)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text("Kata Sandi Lama", color = Color(0xFF007FA3), fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF007FA3)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF009FE3),
                    unfocusedBorderColor = Color(0xFF009FE3),
                    cursorColor = Color(0xFF009FE3),
                    focusedLabelColor = Color(0xFF009FE3)
                )

            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Kata Sandi Baru", color = Color(0xFF007FA3), fontWeight = FontWeight.Medium) },
                textStyle = TextStyle(color = Color(0xFF007FA3)),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF007FA3)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF009FE3),
                    unfocusedBorderColor = Color(0xFF009FE3),
                    cursorColor = Color(0xFF009FE3),
                    focusedLabelColor = Color(0xFF009FE3)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Kata Sandi Baru", color = Color(0xFF007FA3), fontWeight = FontWeight.Medium) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF009FE3),
                    unfocusedBorderColor = Color(0xFF009FE3),
                    cursorColor = Color(0xFF009FE3),
                    focusedLabelColor = Color(0xFF009FE3)
                )
            )

            Spacer(modifier = Modifier.height(300.dp))

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
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock Icon",
                    tint = Color(0xFF009FE3)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Perbarui Kata Sandi",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF009FE3))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGantiKataSandi() {
    GantiKataSandi()

}
