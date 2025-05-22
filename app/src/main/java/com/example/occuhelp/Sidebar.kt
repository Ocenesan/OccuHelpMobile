package com.example.occuhelp

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.occuhelp.ui.OccuHelpTheme

@Composable
fun Sidebar(
    userSessionViewModel: UserSessionViewModel,
    onItemSelected: (route: String) -> Unit, // Callback saat item menu dipilih
    onCloseDrawer: () -> Unit,               // Callback untuk menutup drawer
    onLogout: () -> Unit                     // Callback untuk logout
) {
    val userNameFromVM by userSessionViewModel.loggedInUserName.collectAsStateWithLifecycle()
    val displayName = if (!userNameFromVM.isNullOrBlank()) "dr. $userNameFromVM" else "Pengguna"

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp)
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo dan Tombol Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onCloseDrawer() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "OccuHelp Logo",
                modifier = Modifier
                    .width(200.dp)
                    .height(75.dp)
            )
        }

        // Foto perawat
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(95.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.perawat),
                        contentDescription = "Foto Dokter",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape)
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Menu Items
        val menuItems = listOf(
            // Sesuaikan rute dengan yang ada di Navigation.kt
            SidebarMenuItemData(Icons.Default.Dashboard, "Dashboard", Screen.HomePage.route), // Rute dashboard
            SidebarMenuItemData(Icons.Filled.Info, "Tentang Kami", Screen.AboutUs.route),
            SidebarMenuItemData(Icons.Filled.Group, "Pasien", Screen.Pasien.route),
            SidebarMenuItemData(Icons.Filled.MonitorHeart, "Hasil MCU", Screen.HasilMCU.route),
            //SidebarMenuItemData(Icons.Filled.BarChart, "Rekapitulasi", Screen.Rekapitulasi.route),
            SidebarMenuItemData(Icons.Filled.Flag, "Report", Screen.Report.route),
            //SidebarMenuItemData(Icons.Filled.HeadsetMic, "Layanan Kami", Screen.LayananKami.route),
            //SidebarMenuItemData(Icons.Filled.Call, "Kontak", Screen.Kontak.route)
        )

        menuItems.forEach { item ->
            SidebarMenuItem(
                icon = item.icon,
                label = item.label,
                onClick = { onItemSelected(item.route) } // Panggil callback dengan rute
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Keluar
        OutlinedButton(
            onClick = { onLogout() },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Keluar")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Keluar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

data class SidebarMenuItemData(val icon: ImageVector, val label: String, val route: String)

@Composable
fun SidebarMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Klik pada seluruh baris
            .padding(vertical = 12.dp, horizontal = 24.dp) // Padding lebih besar untuk item menu
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp), // Ukuran ikon
            tint = MaterialTheme.colorScheme.onSurfaceVariant // Warna ikon
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge, // Style teks menu
            color = MaterialTheme.colorScheme.onSurface // Warna teks menu
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Sidebar Content Preview")
@Composable
fun DefaultSidebarPreview() {
    OccuHelpTheme {
        ModalDrawerSheet {
            val previewUserSessionViewModel = UserSessionViewModel().apply {
                setUser("Ayu Lestari Preview") // Set nama pengguna untuk preview
            }
            Sidebar(
                userSessionViewModel = previewUserSessionViewModel,
                onItemSelected = { route -> println("Preview: Item selected - $route") },
                onCloseDrawer = { println("Preview: Close drawer") },
                onLogout = { println("Preview: Logout") }
            )
        }
    }
}