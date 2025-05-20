package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

class RecapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                RecapScreen()
            }
        }
    }
}

val dataRecap= listOf(
    Triple(1, "Sudah", 265),
    Triple(2, "Belum", 50)
)

@Composable
fun RecapScreen(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, recap, dropdown, barChart, tableLayout, guideline1, guideline2, guideline3) = createRefs()

        val options = listOf("Pilihan 1", "Pilihan 2", "Pilihan 3")
        var expanded by remember {
            mutableStateOf(false)
        }
        var selectedOptionText by remember {
            mutableStateOf(options[0])
        }

        val topGuideline = createGuidelineFromTop(0.03f)
        val startGuideline = createGuidelineFromStart(0.05f)
        val endGuideline = createGuidelineFromEnd(0.05f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
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
                .background(
                    OccuHelpBackButtonBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = OccuHelpBackButtonIcon,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "Rekapitulasi",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(recap) {
                top.linkTo(backButton.top)
                start.linkTo(backButton.end, margin = 16.dp)
                bottom.linkTo(backButton.bottom)
            }
        )
        Box(
            modifier = modifier
                .constrainAs(dropdown) {
                    top.linkTo(recap.bottom, margin = 36.dp)
                    end.linkTo(endGuideline)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .widthIn(max = 200.dp)
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pilih",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = modifier.width(100.dp)
            ) {
                options.forEach { label ->
                    DropdownMenuItem(
                        text = { Text(text = label) },
                        onClick = {
                            selectedOptionText = label
                            expanded = false
                        }
                    )
                }
            }
        }
        BarChartData(
            modifier = modifier.constrainAs(barChart) {
                top.linkTo(dropdown.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            }
        )
        Column(
            modifier = modifier.constrainAs(tableLayout) {
                top.linkTo(barChart.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            }
        ) {
            TableLayoutRecap(dataRecap)
        }
    }
}

@Composable
fun TableLayoutRecap(
    RecapScreen: List<Triple<Int, String, Int>>,
    modifier : Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "No.",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(3f),
            )
            Text(
                text = "Jumlah",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(2f),
            )
        }
        LazyColumn {
            items(RecapScreen, key = {it.first}) {
                TableListItem(
                    id = it.first,
                    kategori = it.second,
                    jumlah = it.third
                )
            }
        }
    }
}

@Composable
fun TableListItem(
    id: Int,
    kategori: String,
    jumlah: Int,
    modifier : Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (id % 2 == 0) Color(0xFFC9EBF3) else MaterialTheme.colorScheme.primaryContainer)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = id.toString(),
            textAlign = TextAlign.Center,
            modifier = modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = kategori,
            modifier = modifier.weight(3f),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = jumlah.toString(),
            modifier = modifier.weight(2f),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

data class BarChartRecapModel(
    val label: String,
    val point: Point,
    val description: String
)

val recapData = listOf(
    BarChartRecapModel("", Point(1.5f, 0f), ""),
    BarChartRecapModel("MON", Point(1.5f, 400f), "1300"),
    BarChartRecapModel("TUE", Point(2.5f, 150f), "2000"),
    BarChartRecapModel("WED", Point(3.5f, 350f), "1700"),
    BarChartRecapModel("THU", Point(4.5f, 200f), "1500"),
    BarChartRecapModel("FRI", Point(5.5f, 950f), "2.313"),
    BarChartRecapModel("SAT", Point(6.5f, 100f), "1200"),
    BarChartRecapModel("SUN", Point(7.5f, 350f), "1600"),

    )

@Composable
fun BarChartData(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.large)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pegawai RSUP yang telah \nmelakukan MCU",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Card(
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.inverseOnSurface),
                shape = MaterialTheme.shapes.large,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Weekly",
                        color = Color(0xFF217F96),
                        style = MaterialTheme.typography.displaySmall
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Panah Bawah",
                        tint = Color(0xFF0086B0),
                        modifier = Modifier

                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val barStyle = BarStyle(
            barWidth = 20.dp,
            cornerRadius = 4.dp,
            paddingBetweenBars = 15.dp,
        )
        // Chart
        val xAxisData = AxisData.Builder()
            .startDrawPadding(25.dp)
            .axisOffset(20.dp)
            .axisStepSize(barStyle.barWidth + barStyle.paddingBetweenBars)
            .steps(recapData.size - 1)
            .bottomPadding(20.dp)
            .endPadding(0.dp)
            .labelData { index -> recapData[index].label }
            .axisLineColor(Color(0xFFE5E5EF))
            .axisLabelColor(Color(0xFF615E83))
            .build()

        val yLabels = listOf(" ", "0", "1k", "2k", "3k")

        val yAxisData = AxisData.Builder()
            .steps(yLabels.size - 1)
            .labelAndAxisLinePadding(20.dp)
            .labelData { index -> yLabels[index] }
            .axisLineColor(Color(0xFFE5E5EF))
            .axisLabelColor(Color(0xFF615E83))
            .backgroundColor(MaterialTheme.colorScheme.primaryContainer)
            .build()

        val topBar = recapData.maxByOrNull { it.point.y }

        val barChartData = BarChartData(
            chartData = recapData.map { bar ->
                BarData(
                    point = bar.point,
                    color = if (bar == topBar) Color(0xFF5CC2DB) else Color(0xFF217F96),
                    description = bar.description
                ) },
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            paddingEnd = 0.dp,
            barStyle = barStyle,
            backgroundColor = MaterialTheme.colorScheme.primaryContainer
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            BarChart(
                modifier = Modifier.fillMaxSize(),
                barChartData = barChartData
            )

            topBar?.let { bar ->

                Box(modifier = Modifier
                    .padding(horizontal = 140.dp)
                    .fillMaxSize()) {
                    Text(
                        text = bar.description,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .absoluteOffset(
                                x = with(LocalDensity.current) { (190f - (100f / 2) - 20f).toDp() },
                                y = with(LocalDensity.current) { (65f - 20f - 10.dp.toPx()).toDp() }
                            )
                            .background(Color.Black, RoundedCornerShape(6.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .align(Alignment.TopStart)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RecapPreview() {
    OccuHelpTheme {
        RecapScreen()
    }
}

