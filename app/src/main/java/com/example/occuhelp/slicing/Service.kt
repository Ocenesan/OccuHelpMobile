package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.R
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

class ServiceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                Service()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Service(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
        ) {
            val (backButton, ourService, icon, card1, card2, card3, card4, card5, card6, guideline1, guideline2, guideline3) = createRefs()
            var expanded1 by remember {
                mutableStateOf(false)
            }
            var expanded2 by remember {
                mutableStateOf(false)
            }
            var expanded3 by remember {
                mutableStateOf(false)
            }
            var expanded4 by remember {
                mutableStateOf(false)
            }

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
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(44.dp)
                    .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
                    .constrainAs(backButton) { // ← Tambahkan ini!
                        top.linkTo(topGuideline)
                        start.linkTo(startGuideline)
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon, // Gunakan warna UI spesifik
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "Layanan Kami",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.constrainAs(ourService) {
                    top.linkTo(backButton.top)
                    start.linkTo(backButton.end, margin = 16.dp)
                    bottom.linkTo(backButton.bottom)
                }
            )

            Image(
                painter = painterResource(id = R.drawable.layanan),
                contentDescription = "Icon",
                modifier = modifier
                    .heightIn(107.dp)
                    .widthIn(192.dp)
                    .constrainAs(icon) {
                        top.linkTo(ourService.bottom, margin = 24.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        height = Dimension.wrapContent
                        width = Dimension.wrapContent
                    }
                    .zIndex(4f)
            )

            Card(
                modifier = modifier.constrainAs(card1) {
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    top.linkTo(icon.bottom, margin = (-8).dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Apa itu Medical Check-up (MCU) ")
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("berbasis")
                            }
                            append(" Okupasi?")
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black,
                        textAlign = TextAlign.Justify,
                    )
                    Text(
                        text = "MCU (Medical Check-Up) berbasis okupasi adalah pemeriksaan medis untuk memastikan seseorang memiliki kondisi kesehatan yang memadai guna menjalankan tugas pekerjaan secara aman dan efektif. Pemeriksaan ini disesuaikan dengan jenis pekerjaan, mengingat tiap pekerjaan memiliki risiko kesehatan berbeda.",
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        modifier = modifier.padding(top = 16.dp)
                    )
                }
            }

            Card(
                modifier = modifier
                    .constrainAs(card2) {
                        top.linkTo(card1.bottom, margin = 24.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                    .clickable { expanded1 = !expanded1 },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Pemeriksaan Fisik",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (expanded1) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Icon Toggle",
                            modifier = Modifier.size(36.dp))
                    }
                    AnimatedVisibility(
                        visible = expanded1
                    ) {
                        Text(
                            text = "Lorem ipsum sir dolor amet",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.Black,
                            modifier = modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Card(
                modifier = modifier
                    .constrainAs(card3) {
                        top.linkTo(card2.bottom, margin = 16.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                    .clickable { expanded2 = !expanded2 },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Pemeriksaan Psikologi",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (expanded2) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Icon Toggle",
                            modifier = Modifier.size(36.dp))
                    }
                    AnimatedVisibility(
                        visible = expanded1
                    ) {
                        Text(
                            text = "Lorem ipsum sir dolor amet",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.Black,
                            modifier = modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Card(
                modifier = modifier
                    .constrainAs(card4) {
                        top.linkTo(card3.bottom, margin = 16.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                    .clickable { expanded3 = !expanded3 },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Pemeriksaan Kesehatan Spesifik",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (expanded3) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Icon Toggle",
                            modifier = Modifier.size(36.dp))
                    }
                    AnimatedVisibility(
                        visible = expanded1
                    ) {
                        Text(
                            text = "Lorem ipsum sir dolor amet",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.Black,
                            modifier = modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Card(
                modifier = modifier
                    .constrainAs(card5) {
                        top.linkTo(card4.bottom, margin = 16.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                    .clickable { expanded4 = !expanded4 },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Pemeriksaan Kesehatan Berkala",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (expanded4) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = "Icon Toggle",
                            modifier = Modifier.size(36.dp))
                    }
                    AnimatedVisibility(
                        visible = expanded1
                    ) {
                        Text(
                            text = "Lorem ipsum sir dolor amet",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = Color.Black,
                            modifier = modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Card(
                modifier = modifier.constrainAs(card6) {
                    top.linkTo(card5.bottom, margin = 24.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dokter),
                        contentDescription = "Icon",
                        modifier = modifier
                            .heightIn(min = 90.dp)
                            .widthIn(min = 87.dp)
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ServicePreview() {
    OccuHelpTheme {
        Service()
    }
}