package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CarDetails
import com.example.data.vehicles.IndianVehicleDatabase
import com.example.data.vehicles.IndianVehicleSpec
import com.example.ui.components.dialPhone
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineRedAccent
import com.example.ui.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSetupScreen(
    viewModel: RouteViewModel,
    onSetupComplete: () -> Unit,
    onNavigateBack: () -> Unit = onSetupComplete
) {
    val context = LocalContext.current
    val currentCar by viewModel.carDetails.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("All") }
    var showCustomCarDialog by remember { mutableStateOf(false) }

    // List of dynamic custom vehicles created by user in this session
    val customVehicles = remember { mutableStateListOf<IndianVehicleSpec>() }

    val allVehicles = remember(customVehicles.size) {
        customVehicles + IndianVehicleDatabase.vehicles
    }

    var selectedSpec by remember {
        mutableStateOf(
            allVehicles.find { it.makeModel.equals(currentCar.makeModel, ignoreCase = true) }
                ?: allVehicles.first()
        )
    }

    var registrationInput by remember { mutableStateOf(currentCar.registrationNumber) }
    var fastagInput by remember { mutableStateOf(currentCar.fastagProvider) }

    val filteredVehicles = remember(searchQuery, selectedBrand, allVehicles) {
        allVehicles.filter { spec ->
            val matchesBrand = selectedBrand == "All" || spec.brand.equals(selectedBrand, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    spec.makeModel.contains(searchQuery, ignoreCase = true) ||
                    spec.variant.contains(searchQuery, ignoreCase = true) ||
                    spec.brand.contains(searchQuery, ignoreCase = true) ||
                    spec.category.contains(searchQuery, ignoreCase = true)
            matchesBrand && matchesSearch
        }
    }

    val brandFilters = listOf("All", "Tata", "Mahindra", "Maruti Suzuki", "Hyundai", "Kia", "Toyota", "Honda", "Volkswagen", "MG")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Vehicle Setup",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Linked to ${userProfile.displayName} (${userProfile.email})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("vehicle_setup_back_button")
                    ) {
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
                .testTag("vehicle_setup_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Step 1: Instruction Card with Database Scope info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Pick Your Highway Car",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Auto-constructs tyre pressure targets, FASTag, OEM RSA hotline & in-car SOS protocol.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "⚡ 35+ popular Indian cars stored locally offline. Works even without cell signal on remote highways.",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Step 2: Search Bar & Brand Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Nexon, Creta, Brezza, XUV700, City...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vehicle_search_bar"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(brandFilters) { brand ->
                            FilterChip(
                                selected = selectedBrand == brand,
                                onClick = { selectedBrand = brand },
                                label = { Text(brand, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Step 3: Vehicle Selection Section Header + Add Custom Car Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT VEHICLE (${filteredVehicles.size} available)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    OutlinedButton(
                        onClick = { showCustomCarDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("add_custom_car_button")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Custom Car", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Vehicle Selection Items
            items(filteredVehicles.take(8)) { spec ->
                val isSelected = selectedSpec.makeModel == spec.makeModel
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedSpec = spec }
                        .testTag("vehicle_option_${spec.makeModel.replace(" ", "_")}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = spec.makeModel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = spec.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = spec.variant,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tyres: ${spec.tyreSpec} • ${spec.recommendedPsi}",
                                style = MaterialTheme.typography.labelSmall,
                                color = HighwayGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Step 4: Dynamically Constructed Highway SOS Protocol Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NLineRedAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Constructed SOS for ${selectedSpec.makeModel}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tailored highway emergency protocol configured for this vehicle",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        // In-Car SOS Hardware Instruction
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = NLineRedAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "In-Car Hardware SOS Guide",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NLineRedAccent
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedSpec.sosHardwareInstruction,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // RSA Hotline & National Emergency
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "${selectedSpec.brand} 24x7 RSA",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = selectedSpec.oemRsaPhone,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "NHAI Highway Patrol",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "1033 (Pan-India)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = HighwayGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step 5: Vehicle Registration & FASTag
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Registration & Toll Tag",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = registrationInput,
                            onValueChange = { registrationInput = it.uppercase() },
                            label = { Text("Registration Number") },
                            placeholder = { Text("e.g. KA 01 AB 1234") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("registration_input_field"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = fastagInput,
                            onValueChange = { fastagInput = it },
                            label = { Text("FASTag Provider") },
                            placeholder = { Text("e.g. FASTag Active (ICICI Bank)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fastag_input_field"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Step 6: Confirmation Button
            item {
                Button(
                    onClick = {
                        val configuredDetails = CarDetails(
                            makeModel = selectedSpec.makeModel,
                            variant = selectedSpec.variant,
                            registrationNumber = registrationInput.ifBlank { "KA 01 AB 1234" },
                            tyreSpecification = selectedSpec.tyreSpec,
                            recommendedPsi = selectedSpec.recommendedPsi,
                            hasConnectedCarBluelink = selectedSpec.hasTelematics,
                            fastagProvider = fastagInput.ifBlank { "FASTag Active" },
                            fuelType = selectedSpec.fuelType,
                            oemRsaPhone = selectedSpec.oemRsaPhone,
                            sosButtonInstruction = selectedSpec.sosHardwareInstruction
                        )
                        viewModel.completeVehicleSetup(configuredDetails)
                        onSetupComplete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_vehicle_setup_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Confirm ${selectedSpec.makeModel} & Continue",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Continue"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Modal: Add Custom Vehicle Dialog (When car is not in standard list)
    if (showCustomCarDialog) {
        var customBrand by remember { mutableStateOf("") }
        var customModel by remember { mutableStateOf("") }
        var customVariant by remember { mutableStateOf("") }
        var customCategory by remember { mutableStateOf("SUV") }
        var customTyre by remember { mutableStateOf("") }
        var customPsi by remember { mutableStateOf("33 PSI Cold") }
        var customFuel by remember { mutableStateOf("Petrol") }
        var customRsa by remember { mutableStateOf("1800 102 1800") }
        var customSos by remember { mutableStateOf("Press in-car SOS or dial OEM RSA") }

        Dialog(onDismissRequest = { showCustomCarDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Add Custom Vehicle",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showCustomCarDialog = false },
                            modifier = Modifier.size(28.dp).testTag("close_custom_car_dialog")
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    HorizontalDivider()

                    OutlinedTextField(
                        value = customModel,
                        onValueChange = { customModel = it },
                        label = { Text("Car Make & Model") },
                        placeholder = { Text("e.g. Renault Kiger, Nissan Magnite") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customVariant,
                        onValueChange = { customVariant = it },
                        label = { Text("Variant") },
                        placeholder = { Text("e.g. RXZ 1.0 Turbo CVT") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customTyre,
                        onValueChange = { customTyre = it },
                        label = { Text("Tyre Specification") },
                        placeholder = { Text("e.g. 195/60 R16") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customPsi,
                            onValueChange = { customPsi = it },
                            label = { Text("Target Cold PSI") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = customRsa,
                            onValueChange = { customRsa = it },
                            label = { Text("RSA Helpline") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCustomCarDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val modelName = customModel.ifBlank { "Custom Car" }
                                val brandName = customBrand.ifBlank { modelName.split(" ").first() }
                                val newSpec = IndianVehicleSpec(
                                    brand = brandName,
                                    makeModel = modelName,
                                    variant = customVariant.ifBlank { "Standard" },
                                    category = customCategory,
                                    tyreSpec = customTyre.ifBlank { "Standard Highway Tyres" },
                                    recommendedPsi = customPsi.ifBlank { "33 PSI Cold" },
                                    fuelType = customFuel,
                                    hasTelematics = false,
                                    telematicsName = "Standard Telematics",
                                    oemRsaPhone = customRsa.ifBlank { "1033" },
                                    sosHardwareInstruction = customSos.ifBlank { "Press SOS or dial 1033" }
                                )
                                customVehicles.add(0, newSpec)
                                selectedSpec = newSpec
                                showCustomCarDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save & Select")
                        }
                    }
                }
            }
        }
    }
}
