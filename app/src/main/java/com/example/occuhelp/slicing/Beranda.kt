package com.example.occuhelp.slicing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.R
import com.example.occuhelp.ui.OccuHelpTheme

@Composable
fun Beranda(
    name: String,
    modifier: Modifier = Modifier
) {

    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (dashboardImage, drawerButton, welcomeName, barChart, barChart2, guideline1, guideline2, guideline3)  = createRefs()

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
        Image(
            painter = painterResource(id = R.drawable.dashboard),
            contentDescription = "Dashboard Image",
            modifier = modifier
                .constrainAs(dashboardImage) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .heightIn(min = 181.dp),
            contentScale = ContentScale.FillBounds
        )
        IconButton(
            onClick = {

            },
            modifier = modifier.constrainAs(drawerButton) {
                top.linkTo(topGuideline)
                start.linkTo(startGuideline)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Drawer Menu"
            )
        }
        Text(
            text = "Selamat Datang, \ndr.${name}!",
            fontSize = 24.sp,
            lineHeight = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier.constrainAs(welcomeName) {
                top.linkTo(drawerButton.bottom)
                start.linkTo(drawerButton.end)
            }
        )

        BarChart(
            data = barData,
            modifier = modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .constrainAs(barChart) {
                    top.linkTo(barChart2.bottom, margin = 28.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )

        BarChart(
            data = barData,
            modifier = modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .constrainAs(barChart2) {
                    top.linkTo(dashboardImage.bottom, margin = 28.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )

    }
}

data class BarEntry(val label: String, val value: Int)

val barData = listOf(
    BarEntry("MON", 1600),
    BarEntry("TUE", 700),
    BarEntry("WED", 1800),
    BarEntry("THU", 1000),
    BarEntry("FRI", 2313),
    BarEntry("SAT", 500),
    BarEntry("SUN", 1500)
)

@Composable
fun BarChart(
    data: List<BarEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = Color(0xFF1B6CA8),         // Warna batang default
    highlightColor: Color = Color(0xFF6DD5FA),   // Warna batang tertinggi (biru muda)
    maxBarHeight: Float = 200f,                   // Tinggi maksimum batang (dalam dp)
    yAxisMax: Int = 3000
) {
    val maxValue = yAxisMax
    val yStep = 1000   // Jarak antar label Y

    Column(
        modifier = modifier
            .background(Color(0xFFE6F6F9), RoundedCornerShape(24.dp))
            .padding(14.dp)
            .padding(bottom = 24.dp)
    ) {
        // Sumbu Y (label jumlah pengunjung)
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.height(maxBarHeight.dp + 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (y in (0..maxValue step yStep).reversed()) {
                    Text(
                        text = if (y >= 1000) "${y / 1000}k" else "$y",
                        fontSize = 12.sp,
                        color = Color(0xFF7B8A99)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))

            Row(
                modifier = Modifier
                    .height(maxBarHeight.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEach { entry ->
                    val barHeight = (entry.value.toFloat() / maxValue) * maxBarHeight
                    val isMax = entry.value == data.maxOf { it.value }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Label jumlah di atas batang (khusus batang tertinggi)

                        Box(
                            modifier = Modifier.height(20.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {

                            if (isMax) {
                                Text(
                                    text = "${entry.value}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B2B3C),
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        //batang grafik
                        Canvas(
                            modifier = Modifier
                                .width(26.dp)
                                .height(barHeight.dp)
                        ) {
                            drawRoundRect(
                                color = if (isMax) highlightColor else barColor,
                                size = size,
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = entry.label,
                            fontSize = 14.sp,
                            color = Color(0xFF3C4A5D)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BerandaPreview() {
    OccuHelpTheme {
        Beranda(name = "Ayu Lestari")
    }
}

