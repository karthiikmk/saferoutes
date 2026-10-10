package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppThemeMode
import com.example.data.model.SafeRoutePlan
import com.example.ui.components.HighwayChipsRow
import com.example.ui.components.TripCard
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineBluePrimary
import com.example.ui.theme.NLineRedAccent
import com.example.ui.viewmodel.RouteViewModel

@Composable
fun HomeScreen(
    viewModel: RouteViewModel,
    onNavigateToPlanner: () -> Unit,
    onNavigateToDetail: (SafeRoutePlan) -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToCarDetails: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToVehicleSetup: () -> Unit = {}
) {
    val curatedRoutes = viewModel.curatedRoutes
    val savedRoutes by viewModel.savedRoutes.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val carDetails by viewModel.carDetails.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    var selectedRegion by remember { mutableStateOf("All") }

    val filteredRoutes = remember(selectedRegion, curatedRoutes) {
        when (selectedRegion) {
            "South" -> curatedRoutes.filter { it.title.contains("Chennai") || it.title.contains("Bengaluru") || it.title.contains("Hyderabad") || it.title.contains("Mysuru") }
            "West" -> curatedRoutes.filter { it.title.contains("Mumbai") || it.title.contains("Goa") || it.title.contains("Pune") }
            "North" -> curatedRoutes.filter { it.title.contains("Delhi") || it.title.contains("Jaipur") || it.title.contains("Lucknow") }
            else -> curatedRoutes
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top App Bar with Google Account & Light Mode Switcher
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logged-in Google User Profile Pill (Direct display, no popup sheet)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("home_google_profile_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (userProfile.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = userProfile.photoUrl,
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (userProfile.displayName.isNotBlank()) userProfile.displayName.take(1).uppercase() else "K",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = userProfile.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = userProfile.email,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Action Icons: Theme Toggle (Light / Dark)
                    IconButton(
                        onClick = { viewModel.toggleThemeMode() },
                        modifier = Modifier.testTag("home_theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (themeMode == AppThemeMode.LIGHT) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Toggle Light / Dark Mode",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Hero Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.highway_hero_banner),
                    contentDescription = "Safe highway banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x990B131F),
                                    Color(0xF50B131F)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Safe trip routes across india",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "We choose the safest road, not the shortest.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }

        // Quick Tools Row (Balanced 2-action row with zero wrapping) beneath the image
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToEmergency,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("home_emergency_button"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Emergency Hub",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1033 SOS Hub",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToSaved,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("home_saved_button"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Saved Trips",
                        tint = HighwayGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saved Trips (${savedRoutes.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        // Quick Plan Action
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("plan_new_trip_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = "Plan Route",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Plan Safe Route",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Find divided NH & expressways avoiding bad roads",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = onNavigateToPlanner,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("plan_route_button")
                    ) {
                        Text("Plan")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Dedicated Active Vehicle Card (Tapping opens specs & checklist)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onNavigateToCarDetails() }
                    .testTag("home_vehicle_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = carDetails.makeModel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${carDetails.registrationNumber} • ${carDetails.variant}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tyres: ${carDetails.tyreSpecification} • ${carDetails.recommendedPsi}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Checklist & Specs",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Your Last Trip Section (Only displays if the user has actual saved trips/history)
        if (savedRoutes.isNotEmpty()) {
            item {
                val lastTrip = savedRoutes.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { onNavigateToDetail(lastTrip) }
                        .testTag("home_last_trip_blue_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Recent History",
                                tint = MaterialTheme.colorScheme.primary, // Gorgeous blue highlight icon
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "YOUR LAST TRIP",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = lastTrip.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Row 2: Route Origin → Destination
                        Text(
                            text = "${lastTrip.origin} → ${lastTrip.destination}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Row 3: KM details (distanceApprox)
                        Text(
                            text = lastTrip.distanceApprox,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Row 4: Safety Star Rating (4th Line!)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Safety Rating: ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = getRouteStars(lastTrip),
                                color = Color(0xFFF59E0B), // Vibrant Gold
                                fontWeight = FontWeight.Black,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Row 5: Highways Tags (rendered as vibrant yellow Map-style shields)
                        HighwayChipsRow(verifyLine = lastTrip.verifyLine)
                    }
                }
            }
        }

        // Highway Safety Principles Card (Boxed with header inside)
        item {
            val isDark = MaterialTheme.colorScheme.background == Color(0xFF0B131F) // Deep DarkAsphalt background
            val safetyBgColor = if (isDark) Color(0xFF042F1A) else Color(0xFFE6F4EA)
            val safetyTitleColor = if (isDark) Color(0xFF34D399) else Color(0xFF065F46)
            val safetyBulletTextColor = if (isDark) Color(0xFFD1FAE5) else Color(0xFF064E3B)
            val safetyBulletIconColor = if (isDark) Color(0xFF34D399) else Color(0xFF059669)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = safetyBgColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SAFE HIGHWAY RULES ENFORCED",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = safetyTitleColor,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SafetyRuleBullet(
                        text = "Access-controlled expressways & divided 4/6-lane NH first",
                        textColor = safetyBulletTextColor,
                        iconColor = safetyBulletIconColor
                    )
                    SafetyRuleBullet(
                        text = "Median barriers strictly required — oncoming trucks eliminated",
                        textColor = safetyBulletTextColor,
                        iconColor = safetyBulletIconColor
                    )
                    SafetyRuleBullet(
                        text = "Town crossings bypassed via flyovers or service roads",
                        textColor = safetyBulletTextColor,
                        iconColor = safetyBulletIconColor
                    )
                    SafetyRuleBullet(
                        text = "Verified toll plaza pins lock Maps on main carriageway",
                        textColor = safetyBulletTextColor,
                        iconColor = safetyBulletIconColor
                    )
                    SafetyRuleBullet(
                        text = "Village roads, market cuts & unmarked ghat shortcuts rejected",
                        textColor = safetyBulletTextColor,
                        iconColor = safetyBulletIconColor
                    )
                }
            }
        }

        // Small centered divider between Safety box and Audited Corridors section - Light Gray!
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .width(80.dp)
                        .padding(vertical = 16.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )
            }
        }

        // Section Title & Filters
        item {
            Column(modifier = Modifier.padding(start = 32.dp, end = 16.dp, top = 10.dp, bottom = 2.dp)) {
                Text(
                    text = "Audited Indian Highway Corridors",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pre-computed with verified toll pins, parking & emergency protocols",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "South", "North", "West").forEach { region ->
                        item {
                            FilterChip(
                                selected = selectedRegion == region,
                                onClick = { selectedRegion = region },
                                label = { Text(region) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Corridors List
        items(filteredRoutes) { route ->
            TripCard(
                route = route,
                onClick = { onNavigateToDetail(route) }
            )
        }
    }
}

private fun getRouteStars(route: SafeRoutePlan): String {
    val starRegex = "★+".toRegex()
    val match = starRegex.find(route.verifyLine)
    if (match != null) {
        return match.value
    }
    val roadsString = route.primaryRoads.joinToString(" ").lowercase()
    val titleString = route.title.lowercase()
    val descString = route.distanceApprox.lowercase()
    return when {
        roadsString.contains("expressway") || titleString.contains("expressway") || descString.contains("expressway") ||
        roadsString.contains("ne") || roadsString.contains("outer ring road") || roadsString.contains("orr") -> "★★★★★"
        roadsString.contains("nh") || titleString.contains("nh") -> "★★★★☆"
        else -> "★★★☆☆"
    }
}

@Composable
private fun SafetyRuleBullet(
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    iconColor: Color = HighwayGreen
) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}
