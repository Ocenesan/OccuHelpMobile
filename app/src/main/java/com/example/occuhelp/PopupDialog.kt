package com.example.occuhelp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.occuhelp.ui.OccuHelpTheme

@Composable
fun GenericErrorDialog(
    errorType: LoginPopUpType, // Pass the whole error type
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    confirmButtonText: String = "Coba Lagi" // Allow dynamic button text if needed
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A3B4C) // Dark blue-gray background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(all = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(2.dp, Color.White), CircleShape)
                ) {
                    Icon(
                        imageVector = errorType.icon, // Use icon from errorType
                        contentDescription = "Error Icon",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = errorType.message, // Use message from errorType
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A506B),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp)
                ) {
                    Text(confirmButtonText, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewGenericErrorDialogWrongCredentials() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            GenericErrorDialog(
                errorType = LoginPopUpType.WRONG_CREDENTIALS,
                onDismissRequest = {},
                onConfirmClick = {},
                confirmButtonText = "Coba lagi"
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewGenericErrorDialogNetworkError() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            GenericErrorDialog(
                errorType = LoginPopUpType.NETWORK_ERROR,
                onDismissRequest = {},
                onConfirmClick = {},
                confirmButtonText = "Mengerti" // Example of different button text
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewGenericErrorDialogEmptyTextField() {
    OccuHelpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Gray), contentAlignment = Alignment.Center) {
            GenericErrorDialog(
                errorType = LoginPopUpType.EMPTY_TEXTFIELD,
                onDismissRequest = {},
                onConfirmClick = {},
                confirmButtonText = "Coba lagi" // Example of different button text
            )
        }
    }
}