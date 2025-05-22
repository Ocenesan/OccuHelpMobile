package com.example.occuhelp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(modifier: Modifier = Modifier, onNavigateBack: () -> Unit = {}) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, aboutUs,
            card1, leftIcon, text1, rightIcon, text2,
            card2, card3, card4, worker, text3, text4,
            guideline1, guideline2, guideline3) = createRefs()

        var expanded1 by remember {
            mutableStateOf(false)
        }
        var expanded2 by remember {
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
            onClick = { onNavigateBack() },
            modifier = Modifier
                .size(44.dp)
                .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = OccuHelpBackButtonIcon, // Gunakan warna UI spesifik
                modifier = Modifier.size(28.dp)
            )
        }

        Text(
            text = "Tentang Kami",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(aboutUs) {
                top.linkTo(backButton.top)
                start.linkTo(backButton.end, margin = 16.dp)
                bottom.linkTo(backButton.bottom)
            }
        )

        Card(
            modifier = modifier.constrainAs(card1) {
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                top.linkTo(aboutUs.bottom, margin = 32.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            ConstraintLayout(
                modifier = modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.aboutus1),
                    contentDescription = "Left Icon",
                    modifier = modifier.constrainAs(leftIcon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        width = Dimension.value(80.dp)
                        height = Dimension.value(40.dp)
                    }
                )

                Text(
                    text = "Apa itu OccuHelp?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = modifier.constrainAs(text1) {
                        top.linkTo(parent.top, margin = 24.dp)
                        start.linkTo(parent.start, margin = 38.dp)
                        end.linkTo(rightIcon.start)
                        width = Dimension.fillToConstraints
                    }
                )

                Text(
                    text = "OccuHelp adalah platform digital yang memudahkan tenaga kesehatan dalam mengelola data Medical Check-Up pekerja, menawarkan solusi efisien untuk pencatatan dan pemantauan kesehatan pekerja.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify,
                    modifier = modifier.constrainAs(text2) {
                        top.linkTo(text1.bottom, margin = 8.dp)
                        start.linkTo(parent.start, margin = 38.dp)
                        end.linkTo(rightIcon.start)
                        bottom.linkTo(parent.bottom, margin = 24.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                )

                Image(
                    painter = painterResource(id = R.drawable.aboutus2),
                    contentDescription = "Right Icon",
                    modifier = modifier
                        .heightIn(min = 150.dp)
                        .widthIn(min = 52.dp)
                        .constrainAs(rightIcon) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                )
            }
        }

        Card(
            modifier = modifier
                .constrainAs(card2) {
                    top.linkTo(card1.bottom, margin = 20.dp)
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
                        text = "Visi",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                        imageVector = if (expanded1) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Icon Toggle" )
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
                    top.linkTo(card2.bottom, margin = 12.dp)
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
                        text = "Misi",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                        imageVector = if (expanded1) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Icon Toggle" )
                }
                AnimatedVisibility(
                    visible = expanded2
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
            modifier = modifier.constrainAs(card4) {
                top.linkTo(card3.bottom, margin = 20.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            ConstraintLayout(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 6.dp, end = 6.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.perawat),
                    contentDescription = "Worker",
                    modifier = modifier
                        .heightIn(min = 132.dp)
                        .widthIn(min = 87.dp)
                        .constrainAs(worker) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                )

                Text(
                    text = "Kenapa harus OccuHelp?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = modifier.constrainAs(text3) {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(worker.end, margin = 8.dp)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                )

                Text(
                    text = "OccuHelp mengatasi tantangan pengelolaan data kesehatan pekerja dengan solusi teknologi praktis, memungkinkan tenaga medis fokus pada perawatan pasien tanpa khawatir administrasi data.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify,
                    modifier = modifier.constrainAs(text4) {
                        top.linkTo(text3.bottom, margin = 8.dp)
                        start.linkTo(worker.end, margin = 8.dp)
                        end.linkTo(parent.end, margin = 14.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutUsScreenPreview() {
    OccuHelpTheme {
        AboutUsScreen(onNavigateBack = {})
    }
}