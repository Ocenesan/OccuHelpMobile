package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import com.example.occuhelp.ui.OccuHelpTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon

@OptIn(ExperimentalMaterial3Api::class) // Diperlukan untuk OutlinedTextFieldDefaults
@Composable
fun ForgotPasswordScreen(
    onBackClicked: () -> Unit = {},
    onSendLinkClicked: (String) -> Unit = {},
    currentLoginError: LoginPopUpType?, // Changed from Boolean to LoginErrorType?
    onDismissErrorDialog: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    currentLoginError?.let { errorType ->
        GenericErrorDialog(
            errorType = errorType,
            onDismissRequest = onDismissErrorDialog,
            onConfirmClick = onDismissErrorDialog
            // You could also make confirmButtonText dynamic based on errorType here if needed
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Lupa Kata Sandi?", // Updated text to match "Forgot Password"
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Masukkan email Anda untuk menerima tautan pengaturan ulang kata sandi.", // Updated text
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface // Use onSurface for text on background
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    // style = MaterialTheme.typography.titleMedium, // Already applied by default to label
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = { onSendLinkClicked(email) },
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
                painter = painterResource(id = R.drawable.baseline_verified_24), // Ensure this drawable exists
                contentDescription = "Verifikasi icon",
                modifier = Modifier
                    .size(28.dp)
                    .padding(end = 8.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Text(
                text = "Kirim Link Verifikasi",
                style = MaterialTheme.typography.labelLarge, // Made consistent with LoginScreen button
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreen() {
    OccuHelpTheme {
        ForgotPasswordScreen(
            onBackClicked = {},
            onSendLinkClicked = {},
            currentLoginError = null,
            onDismissErrorDialog = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForgotPasswordScreenWithError() {
    OccuHelpTheme {
        ForgotPasswordScreen(
            onBackClicked = {},
            onSendLinkClicked = {},
            currentLoginError = LoginPopUpType.NETWORK_ERROR, // Example error
            onDismissErrorDialog = {}
        )
    }
}