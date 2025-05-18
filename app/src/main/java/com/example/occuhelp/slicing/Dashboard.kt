package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.common.model.Point
import com.example.occuhelp.R
import com.example.occuhelp.ui.OccuHelpTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                Dashboard(
                    name = "Ayu Lestari"
                )
            }
        }
    }
}

@Composable
fun Dashboard(
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

        BarChartList(
            data = visitorsData,
            modifier = modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .constrainAs(barChart) {
                    top.linkTo(dashboardImage.bottom, margin = 24.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )

        BarChartList(
            data = visitorsData,
            modifier = modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .constrainAs(barChart2) {
                    top.linkTo(barChart.bottom, margin = 24.dp)
                    start.linkTo(startGuideline)
                    end.linkTo(endGuideline)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

data class BarChartDashboardModel(
    val label: String,
    val point: Point,
    val description: String
)

val visitorsData = listOf(
    BarChartDashboardModel("", Point(1.5f, 0f), ""),
    BarChartDashboardModel("MON", Point(1.5f, 400f), "1300"),
    BarChartDashboardModel("TUE", Point(2.5f, 150f), "2000"),
    BarChartDashboardModel("WED", Point(3.5f, 400f), "1700"),
    BarChartDashboardModel("THU", Point(4.5f, 200f), "1500"),
    BarChartDashboardModel("FRI", Point(5.5f, 950f), "2.313"),
    BarChartDashboardModel("SAT", Point(6.5f, 100f), "1200"),
    BarChartDashboardModel("SUN", Point(7.5f, 350f), "1600"),
)

@Composable
fun BarChartList(
    data: List<BarChartDashboardModel>,
    modifier: Modifier = Modifier
) {
    val chunkedData = remember(data) { data.chunked(8) }

    LazyColumn(modifier = modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 12.dp)) {
        items(chunkedData) { chunk ->
            BarChartData(chunk = chunk)
        }
    }
}

@Composable
fun BarChartData(
    chunk: List<BarChartDashboardModel>,
    modifier: Modifier = Modifier
) {
    if (chunk.isEmpty()) {
        Box(
            modifier = modifier
                .height(250.dp)
                .fillMaxWidth()
        ) {  }
        return
    }

    val dataForPlotting = chunk.toList()

    val maxPointModel = dataForPlotting.filter { it.point.y > 0f }
        .maxByOrNull { it.point.y }
    val maxPointValue = maxPointModel?.point

    val barStyle = BarStyle(
        barWidth = 20.dp,
        cornerRadius = 4.dp,
        paddingBetweenBars = 15.dp,
    )

    val actualAxisStepSize = barStyle.barWidth + barStyle.paddingBetweenBars

    val xAxisData = AxisData.Builder()
        .startDrawPadding(25.dp)
        .axisOffset(20.dp)
        .axisStepSize(actualAxisStepSize)
        .steps(if (chunk.isNotEmpty()) chunk.size - 1 else 0) // Jumlah langkah = jumlah item - 1
        .bottomPadding(15.dp)
        .endPadding(actualAxisStepSize)
        .labelData { index -> if (index < chunk.size) chunk[index].label else "" }
        .axisLineColor(Color(0xFFE5E5EF))
        .axisLabelColor(Color(0xFF615E83))
        .build()

    val yLabels = listOf(" ", "0", "1k", "2k", "3k", "4k")

    val yAxisData = AxisData.Builder()
        .steps(yLabels.size - 1)
        .labelAndAxisLinePadding(20.dp)
        .labelData { index -> yLabels[index] }
        .axisLineColor(Color(0xFFE5E5EF))
        .axisLabelColor(Color(0xFF615E83))
        .backgroundColor(MaterialTheme.colorScheme.primaryContainer)
        .build()

    val barItem = dataForPlotting.map { itemDataToPlot ->

        BarData(
            point = itemDataToPlot.point,
            color = if (itemDataToPlot.point == maxPointValue && itemDataToPlot.point.y > 0f) Color(0xFF5CC2DB) else Color(0xFF217F96),
            label = itemDataToPlot.label,
            dataCategoryOptions = DataCategoryOptions(),
            description = itemDataToPlot.description
        )
    }

    val barChartData = co.yml.charts.ui.barchart.models.BarChartData(
        chartData = barItem,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        paddingEnd = 0.dp,
        barStyle = barStyle,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
    )

    Box(
        modifier = modifier.clip(MaterialTheme.shapes.medium)
    ) {
        Box(
            modifier = modifier
                .clip(MaterialTheme.shapes.medium)
        ) {
            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .height(250.dp),
                barChartData = barChartData
            )

            if (maxPointModel != null && maxPointModel.point.y > 0f) {

                val xOffsetPx = 200f
                val yOffsetPx = 60f
                val textWidthPx = 100f
                    Box(modifier = Modifier
                        .padding(horizontal = 143.dp)
                        .fillMaxSize()) {
                        Text(
                            text = maxPointModel.description,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 7.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .absoluteOffset(
                                    x = with(LocalDensity.current) { (xOffsetPx - (textWidthPx / 2) - 20f ).toDp() },
                                    y = with(LocalDensity.current) { (yOffsetPx - 20f - 10.dp.toPx()).toDp() }
                                )
                                .background(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    OccuHelpTheme {
        Dashboard(
            name = "Ayu Lestari"
        )
    }
}