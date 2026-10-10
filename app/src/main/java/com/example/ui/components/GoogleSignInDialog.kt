package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CarDetails
import com.example.data.model.GoogleUserProfile
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineBluePrimary
import com.example.ui.theme.NLineRedAccent

data class VehiclePreset(
    val makeModel: String,
    val variant: String,
    val tyreSpec: String,
    val recommendedPsi: String,
    val hasBluelink: Boolean,
    val fuelType: String,
    val badge: String
)

val popularVehiclePresets = listOf(
    VehiclePreset(
        makeModel = "Tata Nexon",
        variant = "Fearless+ 1.2 Turbo / EV",
        tyreSpec = "215/60 R16 Aero Alloys",
        recommendedPsi = "32-34 PSI Cold",
        hasBluelink = false,
        fuelType = "Turbo Petrol / Diesel / EV",
        badge = "SUV"
    ),
    VehiclePreset(
        makeModel = "Hyundai Creta",
        variant = "SX(O) 1.5 Petrol / Diesel",
        tyreSpec = "215/60 R17 Diamond Cut",
        recommendedPsi = "33-35 PSI Cold",
        hasBluelink = true,
        fuelType = "Petrol / Diesel",
        badge = "SUV"
    ),
    VehiclePreset(
        makeModel = "Mahindra XUV700",
        variant = "AX7L 2.0 Turbo / 2.2 Diesel",
        tyreSpec = "235/60 R18 Diamond Cut",
        recommendedPsi = "36 PSI Cold",
        hasBluelink = true,
        fuelType = "Turbo Petrol / Diesel",
        badge = "SUV"
    ),
    VehiclePreset(
        makeModel = "Hyundai Verna 1.5 Turbo",
        variant = "SX(O) Turbo DCT",
        tyreSpec = "205/55 R16 Black Alloys",
        recommendedPsi = "33-35 PSI Cold",
        hasBluelink = true,
        fuelType = "Turbo Petrol (95 Octane)",
        badge = "Sedan"
    ),
    VehiclePreset(
        makeModel = "Volkswagen Virtus GT",
        variant = "1.5 TSI Evo DSG",
        tyreSpec = "205/55 R16 Razor Alloys",
        recommendedPsi = "33 PSI Cold",
        hasBluelink = false,
        fuelType = "Turbo Petrol (95 Octane)",
        badge = "Sedan"
    ),
    VehiclePreset(
        makeModel = "Skoda Slavia 1.5 TSI",
        variant = "Monte Carlo DSG",
        tyreSpec = "205/55 R16 Black Alloys",
        recommendedPsi = "33 PSI Cold",
        hasBluelink = false,
        fuelType = "Turbo Petrol (95 Octane)",
        badge = "Sedan"
    ),
    VehiclePreset(
        makeModel = "Tata Safari / Harrier",
        variant = "XZA+ 2.0 Kryotec Diesel",
        tyreSpec = "235/60 R18 High-Profile",
        recommendedPsi = "36 PSI Cold",
        hasBluelink = false,
        fuelType = "Turbo Diesel",
        badge = "SUV"
    ),
    VehiclePreset(
        makeModel = "Mahindra XUV700",
        variant = "AX7L 2.0 Turbo Petrol / Diesel",
        tyreSpec = "235/60 R18 Diamond Cut",
        recommendedPsi = "36 PSI Cold",
        hasBluelink = false,
        fuelType = "Petrol / Diesel",
        badge = "SUV"
    ),
    VehiclePreset(
        makeModel = "Kia Seltos X-Line",
        variant = "1.5 Turbo GDi DCT",
        tyreSpec = "215/55 R18 Crystal Alloys",
        recommendedPsi = "35 PSI Cold",
        hasBluelink = false,
        fuelType = "Turbo Petrol (95 Octane)",
        badge = "SUV"
    )
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoogleSignInDialog(
    currentUser: GoogleUserProfile,
    currentCar: CarDetails,
    onDismiss: () -> Unit,
    onSignInSuccess: (email: String, name: String, carDetails: CarDetails) -> Unit,
    onSignOut: () -> Unit = {}
) {
    var emailInput by remember { mutableStateOf(if (currentUser.isLoggedIn) currentUser.email else "") }
    var nameInput by remember { mutableStateOf(if (currentUser.isLoggedIn) currentUser.displayName else "") }

    // Car Details States
    var selectedPresetName by remember { mutableStateOf(currentCar.makeModel) }
    var makeModelInput by remember { mutableStateOf(currentCar.makeModel) }
    var variantInput by remember { mutableStateOf(currentCar.variant) }
    var regNumInput by remember { mutableStateOf(currentCar.registrationNumber) }
    var tyreSpecInput by remember { mutableStateOf(currentCar.tyreSpecification) }
    var recommendedPsiInput by remember { mutableStateOf(currentCar.recommendedPsi) }
    var fastagInput by remember { mutableStateOf(currentCar.fastagProvider) }
    var bluelinkActive by remember { mutableStateOf(currentCar.hasConnectedCarBluelink) }
    var fuelTypeInput by remember { mutableStateOf(currentCar.fuelType) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("google_signin_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header: Google Account & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentUser.isLoggedIn) "Google Account Active" else "Sign in with Google",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentUser.isLoggedIn) currentUser.email else "Select Google account & pick vehicle",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // If currently logged in, show Logout / Switch button
                if (currentUser.isLoggedIn) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Current Driver: ${currentUser.displayName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = currentUser.email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = {
                                    onSignOut()
                                    emailInput = ""
                                    nameInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NLineRedAccent),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("dialog_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Out", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Section 1: Switch or Login with Google Account
                Text(
                    text = "1. Choose or Enter Google Account",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Enter your Google Account email and name manually:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Google Email Address") },
                    placeholder = { Text("user@gmail.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("signin_email_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Driver / Account Name") },
                    placeholder = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("signin_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                // Section 2: Interactive Vehicle Picker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = NLineRedAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Pick Your Vehicle (Updates 4th Tab)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = NLineRedAccent
                        )
                    }
                }

                Text(
                    text = "Tap a popular Indian highway vehicle below to auto-populate tyre, PSI, and telematics specs:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Vehicle Presets Flow Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularVehiclePresets.forEach { preset ->
                        val isSelected = selectedPresetName == preset.makeModel
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                            modifier = Modifier
                                .clickable {
                                    selectedPresetName = preset.makeModel
                                    makeModelInput = preset.makeModel
                                    variantInput = preset.variant
                                    tyreSpecInput = preset.tyreSpec
                                    recommendedPsiInput = preset.recommendedPsi
                                    bluelinkActive = preset.hasBluelink
                                    fuelTypeInput = preset.fuelType
                                }
                                .testTag("preset_vehicle_${preset.makeModel.replace(" ", "_")}")
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = preset.makeModel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = preset.badge,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${preset.tyreSpec.split(" ").take(2).joinToString(" ")} • ${preset.recommendedPsi}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Editable Vehicle Details for Customization
                OutlinedTextField(
                    value = makeModelInput,
                    onValueChange = {
                        makeModelInput = it
                        selectedPresetName = it
                    },
                    label = { Text("Car Make & Model") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("car_make_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = variantInput,
                        onValueChange = { variantInput = it },
                        label = { Text("Variant") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("car_variant_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = regNumInput,
                        onValueChange = { regNumInput = it },
                        label = { Text("Registration No.") },
                        singleLine = true,
                        modifier = Modifier.weight(1.1f).testTag("car_reg_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tyreSpecInput,
                        onValueChange = { tyreSpecInput = it },
                        label = { Text("Tyre Specification") },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f).testTag("car_tyre_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = recommendedPsiInput,
                        onValueChange = { recommendedPsiInput = it },
                        label = { Text("Cold Target PSI") },
                        singleLine = true,
                        modifier = Modifier.weight(0.9f).testTag("car_psi_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                OutlinedTextField(
                    value = fastagInput,
                    onValueChange = { fastagInput = it },
                    label = { Text("FASTag Account Provider") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("car_fastag_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Bluelink / Telematics Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Connected Car Telematics (e.g. Bluelink)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enables console RSA & SOS button assistance",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = bluelinkActive,
                        onCheckedChange = { bluelinkActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = HighwayGreen
                        ),
                        modifier = Modifier.testTag("bluelink_toggle_switch")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Actions: Save & Login Button
                Button(
                    onClick = {
                        val updatedCar = CarDetails(
                            makeModel = makeModelInput.ifBlank { "Hyundai i20 N Line" },
                            variant = variantInput.ifBlank { "N8 Turbo" },
                            registrationNumber = regNumInput.ifBlank { "TN 07 CM 4521" },
                            tyreSpecification = tyreSpecInput.ifBlank { "195/55 R16 Alloys" },
                            recommendedPsi = recommendedPsiInput.ifBlank { "33-35 PSI Cold" },
                            hasConnectedCarBluelink = bluelinkActive,
                            fastagProvider = fastagInput.ifBlank { "Active FASTag" },
                            fuelType = fuelTypeInput
                        )
                        val effectiveEmail = emailInput.ifBlank { "" }
                        val effectiveName = nameInput.ifBlank { "Driver" }
                        onSignInSuccess(effectiveEmail, effectiveName, updatedCar)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_signin_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save and Sign In",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (currentUser.isLoggedIn) "Update Vehicle & Account" else "Sign In with Google & Save Vehicle",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
