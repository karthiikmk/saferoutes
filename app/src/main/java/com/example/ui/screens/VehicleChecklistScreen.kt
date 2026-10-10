package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppThemeMode
import com.example.data.model.CarDetails
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineBluePrimary
import com.example.ui.theme.NLineRedAccent
import com.example.ui.viewmodel.RouteViewModel

data class VehicleCheckItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isCritical: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleChecklistScreen(
    viewModel: RouteViewModel,
    onNavigateBack: () -> Unit
) {
    val carDetails by viewModel.carDetails.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    var showEditCarDialog by remember { mutableStateOf(false) }

    val checkedStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            put("tyre_pressure", true)
            put("fastag", true)
            put("bluelink_active", true)
        }
    }

    // Dynamic checklist tailored specifically to the user's car details
    val dynamicChecklist = remember(carDetails) {
        listOf(
            VehicleCheckItem(
                id = "tyre_pressure",
                title = "${carDetails.tyreSpecification} • ${carDetails.recommendedPsi}",
                description = "Cold pressure target: ${carDetails.recommendedPsi}. Sports-tuned suspension and alloy rims require optimal pressure to avoid rim bending over bridge expansion joints.",
                icon = Icons.Default.Speed,
                isCritical = true
            ),
            VehicleCheckItem(
                id = "fastag",
                title = "${carDetails.fastagProvider} Balance Check",
                description = "Maintain ₹500+ active balance for vehicle ${carDetails.registrationNumber}. All pass-through highway pins lock at toll plazas on the main highway — avoid blacklisted lane halts.",
                icon = Icons.Default.Payment,
                isCritical = true
            ),
            VehicleCheckItem(
                id = "bluelink_active",
                title = if (carDetails.hasConnectedCarBluelink) "Connected Car (Bluelink) Active" else "Standard Emergency Setup",
                description = if (carDetails.hasConnectedCarBluelink)
                    "Bluelink subscription active. Familiarize with the middle RSA button and left SOS button on the electrochromic rear-view mirror."
                else
                    "Save 1800 102 4645 (Hyundai RSA) and 1033 (NHAI) in speed dial.",
                icon = Icons.Default.Security,
                isCritical = true
            ),
            VehicleCheckItem(
                id = "fuel_check",
                title = "Fuel Grade: ${carDetails.fuelType}",
                description = "High-pressure direct injection turbo engines benefit from clean 95-octane fuel. Prefer COCO (Company Owned Company Operated) pumps on National Highways.",
                icon = Icons.Default.DirectionsCar
            ),
            VehicleCheckItem(
                id = "dashcam",
                title = "Dashcam Operational & SD Card Checked",
                description = "Continuous recording on Indian highways protects against highway scams, lane intrusions, and unlit cargo truck incidents.",
                icon = Icons.Default.Videocam
            ),
            VehicleCheckItem(
                id = "windshield_washer",
                title = "Windshield Washer Surfactant & Wiper Blades",
                description = "Top up with bug-cleaner fluid. Essential for clearing insect splatter and truck tyre spray on multi-lane expressways.",
                icon = Icons.Default.WaterDrop
            ),
            VehicleCheckItem(
                id = "toolkit_jack",
                title = "Spare Tyre, Jack & Hazard Triangle in Boot",
                description = "Confirm spare wheel pressure and red reflective emergency triangle are securely stored in ${carDetails.registrationNumber}'s boot.",
                icon = Icons.Default.Build
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = carDetails.makeModel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${carDetails.variant} • ${carDetails.registrationNumber}",
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
                actions = {
                    // Theme mode quick toggle
                    IconButton(
                        onClick = { viewModel.toggleThemeMode() },
                        modifier = Modifier.testTag("toggle_theme_icon_button")
                    ) {
                        Icon(
                            imageVector = if (themeMode == AppThemeMode.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle Theme"
                        )
                    }

                    // Edit car details button
                    IconButton(
                        onClick = { showEditCarDialog = true },
                        modifier = Modifier.testTag("edit_car_details_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Vehicle",
                            tint = MaterialTheme.colorScheme.primary
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
                .testTag("vehicle_tab_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Active Vehicle Card (Configured from Google login / Onboarding)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("active_vehicle_profile_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Car",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = carDetails.makeModel,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = carDetails.variant,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = carDetails.registrationNumber,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Vehicle Spec Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "TYRE SPEC",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = carDetails.tyreSpecification,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "TARGET PSI",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = carDetails.recommendedPsi,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HighwayGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Google Account Linked Indicator & Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { showEditCarDialog = true }
                                    .weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (userProfile.isLoggedIn) "Linked: ${userProfile.email}" else "Guest Mode (Tap to login)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { showEditCarDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Switch Account", fontSize = 10.sp)
                                }

                                Button(
                                    onClick = { showEditCarDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.testTag("vehicle_tab_pick_car_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = "Pick Car",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pick Car", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Checklist Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tailored Pre-Trip Safety Checklist",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = HighwayGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${checkedStates.values.count { it }}/${dynamicChecklist.size} Ready",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = HighwayGreen
                        )
                    }
                }
            }

            // Dynamic Checklist Items
            items(dynamicChecklist) { item ->
                val isChecked = checkedStates[item.id] == true

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { checkedStates[item.id] = !isChecked }
                        .testTag("vehicle_checklist_${item.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChecked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedStates[item.id] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = HighwayGreen
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (item.isCritical) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = NLineRedAccent.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "CRITICAL",
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NLineRedAccent
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditCarDialog) {
        GoogleSignInDialog(
            currentUser = userProfile,
            currentCar = carDetails,
            onDismiss = { showEditCarDialog = false },
            onSignInSuccess = { email, name, updatedCar ->
                viewModel.signInWithGoogle(email, name, updatedCar)
                showEditCarDialog = false
            },
            onSignOut = {
                viewModel.signOut()
            }
        )
    }
}
