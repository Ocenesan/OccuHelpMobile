package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.R
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

class ContactActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                Contact()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val (backButton, imageContact, card1,
            guideline1, guideline2, guideline3) = createRefs()
        var email by remember {
            mutableStateOf("")
        }
        var telephoneNumber by remember {
            mutableStateOf("")
        }
        var message by remember {
            mutableStateOf("")
        }

        var interactionSource by remember  {mutableStateOf("")}

        val topGuideline = createGuidelineFromTop(0.03f)
        val startGuideline = createGuidelineFromStart(0.05f)
        val endGuideline = createGuidelineFromEnd(0.05f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White)
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
                .background(Color.White)
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
                .background(Color.White)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(44.dp)
                .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
                .constrainAs(backButton) { // ← Tambahkan ini!
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
                .zIndex(1f)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = OccuHelpBackButtonIcon, // Gunakan warna UI spesifik
                modifier = Modifier.size(28.dp)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.contact),
            contentDescription = "Contact Us",
            modifier = modifier
                .constrainAs(imageContact) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .requiredSize(359.dp, 312.dp)
        )

        Card(
            modifier = modifier.constrainAs(card1) {
                top.linkTo(imageContact.bottom, margin = (-35).dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 25.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    placeholder = {
                        Text(
                            "Email",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,) },
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.medium),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = telephoneNumber,
                    onValueChange = { telephoneNumber = it },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    placeholder = {
                        Text(
                            "Nomor Telepon",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,) },
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.medium),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    singleLine = false,
                    maxLines = 10,
                    shape = MaterialTheme.shapes.medium,
                    placeholder = {
                        Text(
                            "Ketik Pesan Anda Di Sini...",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,) },
                    modifier = modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondary, shape = MaterialTheme.shapes.medium)
                        .heightIn(min = 160.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )

                OutlinedButton(
                    onClick = { /*TODO*/ },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    shape = MaterialTheme.shapes.small,
                    modifier = modifier.fillMaxWidth().padding(top = 35.dp)
                ) {
                    Text(
                        text = "Kirim",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactPreview() {
    OccuHelpTheme {
        Contact()
    }
}