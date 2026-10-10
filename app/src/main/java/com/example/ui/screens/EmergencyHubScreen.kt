package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmergencyQuickCallList
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineRedAccent
import com.example.ui.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyHubScreen(
    onNavigateBack: () -> Unit,
    viewModel: RouteViewModel? = null
) {
    val context = LocalContext.current
    val carDetails = viewModel?.carDetails?.collectAsState()?.value
    val vehicleName = carDetails?.makeModel ?: "Highway Vehicle"
    val oemRsa = carDetails?.oemRsaPhone ?: "1800 209 8282"
    val sosInstruction = carDetails?.sosButtonInstruction ?: "Press in-car SOS assist switch or dial OEM RSA / NHAI 1033"
    val regNum = carDetails?.registrationNumber ?: "KA 01 AB 1234"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Emergency & SOS Hub",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tailored for $vehicleName ($regNum)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("emergency_hub_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // In-Car Hardware Protocol Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "CONSTRUCTED VEHICLE PROTOCOL • ${vehicleName.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        EmergencyInfoItem(
                            title = "In-Car Hardware SOS Trigger",
                            desc = sosInstruction
                        )

                        EmergencyInfoItem(
                            title = "$vehicleName 24x7 RSA Hotline: $oemRsa",
                            desc = "Direct manufacturer flatbed towing, fuel drop, battery jumpstart, and tyre assistance across Indian National Highways."
                        )

                        EmergencyInfoItem(
                            title = "NHAI 1033 Pan-India Highway Patrol",
                            desc = "Dial 1033 anywhere on an Indian Expressway or NH for immediate heavy crane tow, accident extrication, and emergency paramedic ambulance."
                        )
                    }
                }
            }

            // Quick Call Dialers
            item {
                Text(
                    text = "One-Tap Highway Helpline Dialers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                EmergencyQuickCallList(
                    rsaTitle = "$vehicleName 24x7 Roadside Assistance",
                    rsaPhone = oemRsa
                )
            }

            // Quick Dispatch SMS share
            item {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "EMERGENCY HIGHWAY DISPATCH: $vehicleName ($regNum) breakdown/accident. Please coordinate with 1033 NHAI Patrol and OEM RSA ($oemRsa)."
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Send Emergency Dispatch"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("send_emergency_dispatch_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NLineRedAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share SOS",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share SOS Dispatch Message", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmergencyInfoItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "• $title",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
