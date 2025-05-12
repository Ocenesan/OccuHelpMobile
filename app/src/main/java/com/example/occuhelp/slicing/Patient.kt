package com.example.occuhelp.slicing

import PatientData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

class PatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OccuHelpTheme {
                Patient()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Patient(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (backButton, patient, searchBar, filterButton, recyclerView, pagination, guideline1, guideline2, guideline3) = createRefs()

        var searchText by remember { mutableStateOf("") }

        val topGuideline = createGuidelineFromTop(0.03f)
        val startGuideline = createGuidelineFromStart(0.05f)
        val endGuideline = createGuidelineFromEnd(0.05f)

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.background)
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
                .background(MaterialTheme.colorScheme.background)
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
                .background(MaterialTheme.colorScheme.background)
                .constrainAs(guideline3) {
                    end.linkTo(endGuideline)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        IconButton(
            onClick = {  },
            modifier = Modifier
                .size(44.dp)
                .background(OccuHelpBackButtonBackground, shape = MaterialTheme.shapes.medium)
                .constrainAs(backButton) { // ← Tambahkan ini!
                top.linkTo(topGuideline)
                start.linkTo(startGuideline)
        }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon, // Gunakan warna UI spesifik
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = "Pasien",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.constrainAs(patient) {
                top.linkTo(backButton.top)
                start.linkTo(backButton.end, margin = 16.dp)
                bottom.linkTo(backButton.bottom)
            }
        )

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = modifier.constrainAs(searchBar) {
                top.linkTo(patient.bottom, margin = 24.dp)
                start.linkTo(startGuideline)
                end.linkTo(filterButton.start, margin = 8.dp)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedLabelColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                placeholderColor = MaterialTheme.colorScheme.secondary,
                leadingIconColor = MaterialTheme.colorScheme.secondary,
            ),
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    text = "Cari...",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(top = (1).dp)
                )
                          },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon" ,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        )
        OutlinedButton(
            onClick = { /*TODO*/ },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
            shape = MaterialTheme.shapes.medium,
            modifier = modifier.constrainAs(filterButton) {
                top.linkTo(searchBar.top)
                end.linkTo(endGuideline)
                bottom.linkTo(searchBar.bottom)
                height = Dimension.fillToConstraints
            }

        ) {
            Icon(
                imageVector = Icons.Default.Tune ,
                contentDescription = "Filter Button"
            )
        }
        Box(
            modifier = modifier.constrainAs(recyclerView) {
                start.linkTo(startGuideline)
                top.linkTo(filterButton.bottom, margin = 16.dp)
                end.linkTo(endGuideline)
                width = Dimension.fillToConstraints
                height = Dimension.wrapContent
            }
        ) {
            LazyColumn {
                items(PatientData.patient, key = {it.id}) {patient ->
                    PatientListItem(
                        id = patient.id,
                        name = patient.name,
                        date = patient.date,
                        workUnit = patient.workUnit,
                        examinationType = patient.examinationType
                    )
                }
            }
        }
        Row(
            modifier = modifier.constrainAs(pagination) {
                top.linkTo(recyclerView.bottom)
                start.linkTo(startGuideline)
                end.linkTo(endGuideline)
                height = Dimension.wrapContent
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TextButton(
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Sebelumnya",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
            TextButton(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                modifier = modifier.size(33.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "1", color = Color.White)
            }
            TextButton(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
                modifier = modifier.size(33.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "2", color = MaterialTheme.colorScheme.onSurface)
            }
            TextButton(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
                modifier = modifier.size(33.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "3", color = MaterialTheme.colorScheme.onSurface)
            }
            TextButton(
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Selanjutnya",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}

@Composable
fun PatientListItem(
    id: String,
    name: String,
    date: String,
    workUnit: String,
    examinationType: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = id,
                    fontSize = 12.sp,
                )
                IconButton(
                    onClick = { /*TODO*/ },
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Button",
                        tint = Color(0xFFD9D9D9)
                    )
                }
            }
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = modifier.fillMaxWidth()
            )
            Text(
                text = name,
                style = MaterialTheme.typography.displayMedium,
                modifier = modifier.padding(vertical = 10.dp)
            )
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tgl. Periksa",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Unit Kerja",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = workUnit,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Jenis Pemeriksaan",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = examinationType,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PatientPreview() {
    OccuHelpTheme {
        Patient()
//        PatientListItem(
//            id = "PSN-2401020625",
//            name = "Adelina Maharani Putri",
//            date = "20/11/2023",
//            workUnit = "Instalasi Farmasi",
//            examinationType = "Medical Check-Up", )
    }
}