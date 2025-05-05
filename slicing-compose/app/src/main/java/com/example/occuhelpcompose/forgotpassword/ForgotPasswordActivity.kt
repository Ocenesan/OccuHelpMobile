package com.example.occuhelpcompose.forgotpassword

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
import androidx.compose.material.icons.filled.CheckCircleOutline
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelpcompose.ui.theme.OccuHelpComposeTheme
import com.example.occuhelpcompose.ui.theme.PrimaryText

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpComposeTheme {
                ForgotPassword()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPassword(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, titleText, subTitleText, iFieldEmail, submitButton,
            guideline1, guideline2, guideline3, guideline4) = createRefs()
        var email by remember {
            mutableStateOf("")
        }
        var emailError = email.isEmpty()

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
                    end.linkTo(endGuideline)
                    start.linkTo(startGuideline)
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
            text = "Masukkan Email untuk Verifikasi.",
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
            text = "Silahkan ketik email Anda pada kolom di bawah ini, agar kami bisa mengirimkan",
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
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            label = { Text(text = "Email") },
            modifier = modifier.constrainAs(iFieldEmail) {
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
                keyboardType = KeyboardType.Text
            ),
        )

        OutlinedButton(
            onClick = { /*TODO*/ },
            border = BorderStroke(1.dp, PrimaryText),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.constrainAs(submitButton) {
                bottom.linkTo(bottomGuideline)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircleOutline,
                    contentDescription = "Icon Check",
                    modifier.size(18.dp)
                )
                Text(text = "Kirim Link Verifikasi", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    OccuHelpComposeTheme {
        ForgotPassword()
    }
}