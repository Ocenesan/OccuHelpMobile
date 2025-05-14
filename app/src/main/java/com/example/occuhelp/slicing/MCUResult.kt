package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

class MCUResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                PatientDetailsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

val data = listOf(
    Triple(1, "Umur", "29 Tahun"),
    Triple(2, "Riwayat Kesehatan Pekerja", "15/06/2022"),
    Triple(3, "Tekanan Darah", "22/05/2022"),
    Triple(4, "Status Gizi", "15/06/2022"),
    Triple(5, "Hemoglobin", "22/05/2022"),
    Triple(6, "Glukosa", "15/06/2022"),
    Triple(7, "Gangguan EKG (jantung)", "22/05/2022"),
    Triple(8, "Kolestrol", "15/06/2022"),
    Triple(9, "HDL (Lemak Baik)", "22/05/2022")
)

@Composable
fun McuResultScreen(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        val (backButton, divider, tableLayout, patient, biodataRow, guideline1, guideline2, guideline3) = createRefs()

        val topGuideline = createGuidelineFromTop(0.02f)
        val startGuideline = createGuidelineFromStart(0.03f)
        val endGuideline = createGuidelineFromEnd(0.03f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(2.dp)
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
                .width(2.dp)
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
                .width(2.dp)
                .background(Color.White)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        // Back Button
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

        // Patient Name (Judul)
        Text(
            text = "Hasil MCU Pasien",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(patient) {
                top.linkTo(topGuideline)
                start.linkTo(backButton.end, margin = 16.dp)
                bottom.linkTo(backButton.bottom)
            }
        )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(biodataRow) {
                        top.linkTo(patient.bottom, margin = 34.dp)
                        start.linkTo(startGuideline)
                        end.linkTo(endGuideline)
                        width = Dimension.fillToConstraints
                    }
            ) {

                Text(
                    text = "Adelina Maharani Putri",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(3f),
                )

                Text(
                    text = "#Biodata Pasien",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

        Spacer(modifier = Modifier.height(40.dp))

        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = modifier.constrainAs(divider) {
                top.linkTo(biodataRow.bottom, margin = 8.dp)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
            }
        )
            Spacer(modifier = Modifier.height(12.dp))

            // Table Header
            Column(
                modifier = modifier.constrainAs(tableLayout) {
                    top.linkTo(divider.bottom, margin = 24.dp)
                    start.linkTo(startGuideline, margin = 8.dp)
                    end.linkTo(endGuideline, margin = 8.dp)
                    width = Dimension.fillToConstraints
            }
        )
            {
                TableLayout(data)
            }
            }
        }

@Composable
fun TableLayout(
    mcuResult: List<Triple<Int, String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween

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
                text = "Hasil Analisa",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(2f),
            )
        }
        LazyColumn {
            items(mcuResult, key = { it.first }) {
                TableListItem(
                    id = it.first,
                    category = it.second,
                    analysisResult = it.third
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {},
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .width(180.dp) // Lebar tombol lebih kecil
                    .height(39.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Detail",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Lihat Detail",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TableListItem(
    id: Int,
    category: String,
    analysisResult: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(if (id % 2 == 0) MaterialTheme.colorScheme.primaryContainer else Color.White)
            .padding(vertical = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = id.toString(),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = category,
            modifier = Modifier.weight(3f),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = analysisResult,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.labelSmall

        )
    }
}

@Preview(showBackground = true)
@Composable
fun MCUResultPreview() {
    OccuHelpTheme {
        McuResultScreen()
    }
}