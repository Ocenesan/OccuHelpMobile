package com.example.occuhelpcompose.resetpassword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelpcompose.R
import com.example.occuhelpcompose.ui.theme.OccuHelpComposeTheme
import com.example.occuhelpcompose.ui.theme.PrimaryText

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpComposeTheme {
                ResetPassword()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, titleText, subTitleText, iOldPassword, iNewPassword,
            iConfirmationPassword, guideline1, guideline2,
            guideline3, guideline4, buttonUpdate) = createRefs()
        var oldPassword by remember {
            mutableStateOf("")
        }
        var newPassword by remember {
            mutableStateOf("")
        }
        var confirmationPassword by remember {
            mutableStateOf("")
        }
        var oldPasswordVisible by remember {
            mutableStateOf(false)
        }
        var newPasswordVisible by remember {
            mutableStateOf(false)
        }
        var confirmationPasswordVisible by remember {
            mutableStateOf(false)
        }

        var oldPasswordError = oldPassword.isEmpty()
        var newPasswordError = newPassword.isEmpty()
        var confirmationPasswordError = confirmationPassword.isEmpty()

        val topGuideline = createGuidelineFromTop(0.03f)
        val startGuideline = createGuidelineFromStart(0.05f)
        val endGuideline = createGuidelineFromEnd(0.05f)
        val bottomGuideline = createGuidelineFromBottom(0.20f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Red)
                .constrainAs(guideline1) {
                    top.linkTo(topGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline2) {
                    start.linkTo(startGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Red)
                .constrainAs(guideline4) {
                    bottom.linkTo(bottomGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        IconButton(
            onClick = { /*TODO*/ },
            modifier = modifier
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button",
                Modifier.size(32.dp)
            )
        }

        Text(
            text = "Silahkan Ganti Kata Sandi Anda!",
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 50.sp,
            color = PrimaryText,
            modifier = modifier.constrainAs(titleText) {
                top.linkTo(backButton.bottom, margin = 16.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        )

        Text(
            text = "Identitasmu telah diverifikasi.\n" +
                    "Silahkan masukkan kata sandi yang baru!",
            color = Color(0xFF185C6D),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 14.sp,
            modifier = modifier.constrainAs(subTitleText) {
                top.linkTo(titleText.bottom, margin = 16.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        )

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            singleLine = true,
            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(text = "Kata Sandi Lama") },
            modifier = modifier.constrainAs(iOldPassword) {
                top.linkTo(subTitleText.bottom, margin = 32.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryText,
                unfocusedLabelColor = PrimaryText
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Password",
                        Modifier.size(24.dp)
                    )
                }
            }
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            singleLine = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(text = "Kata Sandi Baru") },
            modifier = modifier.constrainAs(iNewPassword) {
                top.linkTo(iOldPassword.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryText,
                unfocusedLabelColor = PrimaryText
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Password",
                        Modifier.size(24.dp)
                    )
                }
            }
        )

        OutlinedTextField(
            value = confirmationPassword,
            onValueChange = { confirmationPassword = it },
            singleLine = true,
            visualTransformation = if (confirmationPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(text = "Konfirmasi Kata Sandi Baru") },
            modifier = modifier.constrainAs(iConfirmationPassword) {
                top.linkTo(iNewPassword.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryText,
                unfocusedLabelColor = PrimaryText
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Password",
                        Modifier.size(24.dp)
                    )
                }
            }
        )

        OutlinedButton(
            onClick = { /*TODO*/ },
            border = BorderStroke(1.dp, PrimaryText),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.constrainAs(buttonUpdate) {
                bottom.linkTo(bottomGuideline)
                end.linkTo(endGuideline)
                start.linkTo(startGuideline)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription ="Icon Lock",
                    modifier.size(18.dp)
                )
                Text(text = "Perbarui Kata Sandi", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetPasswordReview() {
    OccuHelpComposeTheme {
        ResetPassword()
    }
}