package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentLoginError: LoginPopUpType?,
    onDismissErrorDialog: () -> Unit,
    onLoginClicked: (String, String) -> Unit,
    onChangePasswordClicked: () -> Unit,
    isLoading: Boolean
) {
    var nip by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    currentLoginError?.let { errorType ->
        GenericErrorDialog(
            errorType = errorType,
            onDismissRequest = onDismissErrorDialog,
            onConfirmClick = onDismissErrorDialog
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 40.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "OccuHelp Logo",
            modifier = Modifier
                .width(290.dp)
                .height(83.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input field untuk NIP
        OutlinedTextField(
            value = nip,
            onValueChange = { nip = it },
            label = {
                Text(
                    "NIP",
                    // style = MaterialTheme.typography.titleMedium, // Font & size dari titleMedium
                    color = MaterialTheme.colorScheme.primary // Warna label
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium, // Bentuk dari tema
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge, // Style untuk teks yang diketik
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input field untuk Kata Sandi
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Kata Sandi",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium, // Bentuk dari tema
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Sembunyikan kata sandi" else "Tampilkan kata sandi",
                        tint = MaterialTheme.colorScheme.primary // Warna ikon mata dari tema
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge, // Style untuk teks yang diketik
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Ubah Kata Sandi?",
                style = MaterialTheme.typography.bodySmall, // Style (termasuk warna GrayText) dari tema
                modifier = Modifier.clickable(enabled = !isLoading) {
                    onChangePasswordClicked()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { onLoginClicked(nip, password) },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary), // Atau outline jika beda
            shape = MaterialTheme.shapes.small, // Atau medium, sesuaikan dengan desain tombol
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_login_24),
                    contentDescription = "Masuk Icon",
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Masuk",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    OccuHelpTheme { // Bungkus Preview juga dengan tema
        LoginScreen(
            currentLoginError = null,
            onDismissErrorDialog = {},
            onLoginClicked = { _, _ -> },
            onChangePasswordClicked = {},
            isLoading = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreenWithError() {
    OccuHelpTheme {
        LoginScreen(
            currentLoginError = LoginPopUpType.NETWORK_ERROR,
            onDismissErrorDialog = {},
            onLoginClicked = { _, _ -> },
            onChangePasswordClicked = {},
            isLoading = false
        )
    }
}