package com.example.occuhelp.LogIn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.occuhelp.R

class UpdatedPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KataSandiBerhasilScreen()
        }
    }
}

@Composable
fun KataSandiBerhasilScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
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
            text = "Kata sandi Anda berhasil diperbarui. \nSilahkan Login kembali.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF007FA3)
            ),
            fontSize = 14.sp,
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .background(color = Color.White, shape = CircleShape)
                .border(8.dp, Color(0xFF299BB8), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Berhasil",
                tint = Color(0xFF299BB8),
                modifier = Modifier.size(64.dp)
            )
        }

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
            Image(
                painter = painterResource(id = R.drawable.baseline_login_24),
                contentDescription = "Login Icon",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp),
                colorFilter = ColorFilter.tint(Color(0xFF009ECF))
            )
            Text(
                text = "Masuk",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color(0xFF009FE3)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KataSandiBerhasilPreview() {
    KataSandiBerhasilScreen()
}
