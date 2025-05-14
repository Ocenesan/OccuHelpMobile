package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpPrimaryContainer
import com.example.occuhelp.ui.OccuHelpTheme

class ReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                Report()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

val dataReport = listOf(
    Triple(1, "Normal", "13/05/2022"),
    Triple(2, "Tidak Normal", "13/05/2022"),
    Triple(3, "Tidak Normal", "13/05/2022"),
    Triple(4, "Tidak Normal", "13/05/2022"),
    Triple(5, "Tidak Normal", "13/05/2022"),
    Triple(6, "Tidak Normal", "13/05/2022")
)

@Composable
fun Report(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, report, textInput1, textInput2, buttonInput1, tableLayout, buttonInput2, buttonNext, guideline1, guideline2, guideline3) = createRefs()

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
            modifier = modifier
                .constrainAs(backButton) {
                    top.linkTo(topGuideline)
                    start.linkTo(startGuideline)
                }
                .size(44.dp)
                .background(
                    OccuHelpBackButtonBackground,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = OccuHelpBackButtonIcon,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = "Report",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(report) {
                top.linkTo(backButton.top)
                start.linkTo(backButton.end, margin = 20.dp)
                bottom.linkTo(backButton.bottom)
            }
        )
        Text(
            text = "Periode Tanggal Input",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(textInput1) {
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                top.linkTo(report.bottom, margin = 40.dp)
            }
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = modifier
                .height(30.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(50)).constrainAs(buttonInput1) {
                start.linkTo(textInput1.start)
                end.linkTo(textInput1.end)
                top.linkTo(textInput1.bottom, margin = 8.dp)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
            contentPadding = PaddingValues(horizontal = 0.dp), // Agar konten menempel ke sisi
            elevation = null //
        ) {
                Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd // ikon di ujung kanan tengah
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    OccuHelpPrimaryContainer,
                                    shape = MaterialTheme.shapes.medium
                                )
                        ) {

                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Date Icon",
                                tint = OccuHelpBackButtonIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }


        }
        Text(
            text = "Sampai",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(textInput2) {
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                top.linkTo(buttonInput1.bottom, margin = 16.dp)
            }
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = modifier
                .height(30.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(50))
                .constrainAs(buttonInput2) {
                start.linkTo(textInput1.start)
                end.linkTo(textInput1.end)
                top.linkTo(textInput2.bottom, margin = 8.dp)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            ),
            contentPadding = PaddingValues(horizontal = 0.dp), // Agar konten menempel ke sisi
            elevation = null //
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd // ikon di ujung kanan tengah
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            OccuHelpPrimaryContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date Icon",
                        tint = OccuHelpBackButtonIcon,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

        }
        Column(
            modifier = modifier.constrainAs(tableLayout) {
                top.linkTo(buttonInput2.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            }
        ) {
            TableLayoutReport(
                dataReport
            )
        }
        OutlinedButton(
            onClick = { /*TODO*/ } ,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            modifier = modifier.constrainAs(buttonNext) {
                top.linkTo(tableLayout.bottom, margin = 48.dp)
                end.linkTo(endGuideline)
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assignment,
                    contentDescription ="Icon Report",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Lihat Laporan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TableLayoutReport(
    report: List<Triple<Int, String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "No.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Text(
                text = "File Terunduh",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = modifier.weight(3f)
            )
            Text(
                text = "Tanggal",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = modifier.weight(2f)
            )
        }
        LazyColumn {
            items(report, key = {it.first}) {
                TableReportListItem(
                    id = it.first,
                    downloadedFile = it.second,
                    date = it.third
                )
            }
        }
    }
}

@Composable
fun TableReportListItem(
    id: Int,
    downloadedFile: String,
    date: String,
    modifier : Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF7F6FE))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = id.toString(),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = downloadedFile,
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = date,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPreview() {
    OccuHelpTheme {
        Report()
    }
}