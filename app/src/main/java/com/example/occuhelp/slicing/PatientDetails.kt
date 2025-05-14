package com.example.occuhelp.slicing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.occuhelp.ui.OccuHelpBackButtonBackground
import com.example.occuhelp.ui.OccuHelpBackButtonIcon
import com.example.occuhelp.ui.OccuHelpTheme

class PatientDetailsActivity : ComponentActivity() {
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
@Composable
fun PatientDetailsScreen() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        val (
            backButton, patientTitle, profileImage,
            patientNameRow, biodataDivider, detailsColumn, mcuButtonRow
        ) = createRefs()

        // Top App Bar Elements
        IconButton(
            onClick = {  },
            modifier = Modifier
                .size(44.dp)
                .background(
                    OccuHelpBackButtonBackground,
                    shape = MaterialTheme.shapes.medium
                )
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start)
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = OccuHelpBackButtonIcon,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = "Detail Pasien",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.constrainAs(patientTitle) {
                top.linkTo(backButton.top)
                bottom.linkTo(backButton.bottom)
                start.linkTo(backButton.end, margin = 16.dp)
            }
        )

        // Foto Profil
        Image(
            painter = painterResource(id = R.drawable.miyamura),
            contentDescription = "Foto Pasien",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredSize(125.dp, 150.dp)
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(profileImage) {
                    top.linkTo(backButton.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Nama dan Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(patientNameRow) {
                    top.linkTo(profileImage.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = "Miyamura Izumi",
                style = MaterialTheme.typography.titleLarge,
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

        // Divider
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 6.dp)
                .constrainAs(biodataDivider) {
                    top.linkTo(patientNameRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        // Detail Biodata
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .constrainAs(detailsColumn) {
                    top.linkTo(biodataDivider.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            DetailRow("Tanggal Pemeriksaan", "29 Maret 2025")
            DetailRow("Nomor Rekam Medis", "RM-20250329001")
            DetailRow("Nomor Pasien", "PSN-882017465")
            DetailRow("Jenis Kelamin", "Perempuan")
            DetailRow("Umur", "28 Tahun")
            DetailRow("Tempat dan Tanggal Lahir", "Jakarta, 15 Januari 1997")
            DetailRow("Alamat Lengkap", "Jl. Cempaka Putih Timur No. 10, Jakarta")
        }

        // Tombol Lihat Hasil MCU
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(mcuButtonRow) {
                    top.linkTo(detailsColumn.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 24.dp)
                    width = Dimension.fillToConstraints
                },
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = { /* Action */ },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 180.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Assignment,
                    contentDescription = "MCU Icon",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lihat Hasil MCU",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

// Fungsi pembantu untuk baris biodata (tidak banyak perubahan, sudah cukup baik)
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PatientDetailsPreview() {
    OccuHelpTheme {
        PatientDetailsScreen()
    }
}