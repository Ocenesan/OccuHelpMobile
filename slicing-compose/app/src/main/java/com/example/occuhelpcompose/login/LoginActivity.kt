package com.example.occuhelpcompose.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpComposeTheme {
                Login()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
    ) {
        val (icon, login, iFieldNip, iFieldPassword, buttonLogin,
            textButton, guideline1, guideline2, guideline3,
            guideline4, guideline5, guideline6) = createRefs()
        var nip by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
        var passwordVisible by remember {
            mutableStateOf(false)
        }
        var nipError = nip.isEmpty()
        var passwordError = password.isEmpty()

        val topGuideline = createGuidelineFromTop(0.07f)
        val startGuideline1 = createGuidelineFromStart(0.12f)
        val endGuideline1 = createGuidelineFromEnd(0.12f)
        val startGuideline2 = createGuidelineFromStart(0.05f)
        val endGuideline2 = createGuidelineFromEnd(0.05f)
        val bottomGuideLine = createGuidelineFromBottom(0.20f)

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
                    start.linkTo(startGuideline1)
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
                    end.linkTo(endGuideline1)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline4) {
                    start.linkTo(startGuideline2)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color.Red)
                .constrainAs(guideline5) {
                    end.linkTo(endGuideline2)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Red)
                .constrainAs(guideline6) {
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    bottom.linkTo(bottomGuideLine)
                }
        )

        Image(
            painter = painterResource(id = R.drawable.icon),
            contentScale = ContentScale.Fit,
            contentDescription = "Icon",
            modifier = modifier.constrainAs(icon) {
                top.linkTo(topGuideline)
                start.linkTo(startGuideline1)
                end.linkTo(endGuideline1)
                width = Dimension.fillToConstraints
                height = Dimension.percent(0.1f)
            }
        )

        Text(
            text = "Login",
            color = PrimaryText,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            modifier = modifier.constrainAs(login) {
                top.linkTo(icon.bottom, margin = 24.dp)
                start.linkTo(startGuideline1)
                end.linkTo(endGuideline1)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }
        )

        OutlinedTextField(
            value = nip,
            onValueChange = { nip = it },
            singleLine = true,
            label = { Text(text = "NIP") },
            modifier = modifier.constrainAs(iFieldNip) {
                top.linkTo(login.bottom, margin = 32.dp)
                start.linkTo(startGuideline2)
                end.linkTo(endGuideline2)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryText,
                unfocusedLabelColor = PrimaryText
            ),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            label = { Text(text = "Password") },
            modifier = modifier.constrainAs(iFieldPassword) {
                top.linkTo(iFieldNip.bottom, margin = 24.dp)
                start.linkTo(startGuideline2)
                end.linkTo(endGuideline2)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = PrimaryText,
                unfocusedLabelColor = PrimaryText
            ),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Password",
                        Modifier.size(24.dp)
                    )
                }
            }
        )

        TextButton(
            onClick = { /*TODO*/ },
            modifier = modifier.constrainAs(textButton) {
                top.linkTo(iFieldPassword.bottom)
                end.linkTo(endGuideline2)
            },
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = "Ubah Kata Sandi?")
        }

        OutlinedButton(
            onClick = { /*TODO*/ },
            border = BorderStroke(1.dp, PrimaryText),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier.constrainAs(buttonLogin) {
                bottom.linkTo(bottomGuideLine)
                end.linkTo(endGuideline2)
                start.linkTo(startGuideline2)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = modifier,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.vector_masuk),
                    contentDescription ="Icon Login",
                    modifier.size(18.dp)
                )
                Text(text = "Masuk", fontSize = 18.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    OccuHelpComposeTheme {
        Login()
    }
}