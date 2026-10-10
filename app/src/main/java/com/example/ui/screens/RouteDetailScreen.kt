package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import com.example.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.TripOrigin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.example.ui.components.EmergencyQuickCallList
import com.example.ui.components.HighwayChipsRow
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SafeRoutePlan
import com.example.ui.components.dialPhone
import com.example.ui.theme.HighwayGreen
import com.example.ui.theme.NLineBluePrimary
import com.example.ui.theme.NLineRedAccent
import com.example.ui.viewmodel.RouteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(
    plan: SafeRoutePlan,
    viewModel: RouteViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val carDetails by viewModel.carDetails.collectAsState()
    val savedRoutes by viewModel.savedRoutes.collectAsState()
    val isSaved = savedRoutes.any { it.title == plan.title || it.id == plan.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${carDetails.makeModel} • Safe Route Plan",
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
                    IconButton(
                        onClick = { viewModel.saveCurrentRoute() },
                        modifier = Modifier.testTag("save_route_top_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) HighwayGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = { viewModel.copyPromptOutput(context, plan) },
                        modifier = Modifier.testTag("copy_route_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Route"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.shareRoute(context, plan) },
                        modifier = Modifier.testTag("share_route_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Route"
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
                .testTag("route_detail_scroll_view"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Primary Open in Google Maps Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("maps_action_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOCKED HIGHWAY MAPS LINK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Image(
                            painter = painterResource(id = R.drawable.highway_hero_banner),
                            contentDescription = "Static Map Route Placeholder",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = plan.mapsUrl,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openMaps(context, plan.mapsUrl) },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .testTag("open_google_maps_button"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open Maps",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open in Google Maps", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.copyPromptOutput(context, plan) },
                                modifier = Modifier.weight(0.7f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Copy Link")
                            }
                        }
                    }
                }
            }

            // 🗺️ SECTION 1: ROUTE
            item {
                SectionCard(
                    title = "🗺️ Route",
                    subtitle = "Preview stays on named expressway, NH, or divided bypass"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "• Check: preview stays on the named expressway, NH, or divided bypass",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• If no directions: search destination → Directions → set start → Add stop for each pin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Start point
                        RoutePointRow(
                            label = "Start",
                            title = plan.origin,
                            road = plan.primaryRoads.firstOrNull() ?: "Divided NH",
                            isPassThrough = false,
                            isStart = true
                        )

                        // Stops / Pass-through Pins
                        plan.stops.forEach { stop ->
                            RoutePointRow(
                                label = "Pass-through Pin",
                                title = stop.placeName,
                                road = stop.roadNumber,
                                isPassThrough = true,
                                isStart = false
                            )
                        }

                        // Destination
                        RoutePointRow(
                            label = "Destination",
                            title = plan.destination,
                            road = "Final stop",
                            isPassThrough = false,
                            isDestination = true
                        )
                    }
                }
            }

            item { CenteredSectionDivider() }

            // 📝 SECTION 2: TIPS
            item {
                SectionCard(
                    title = "📝 Tips",
                    subtitle = "Curated driving corridor & timing notes"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipItem(label = "Best time", text = plan.tips.bestTime)
                        TipItem(label = "Avoid", text = plan.tips.avoid)
                        TipItem(label = "Local note", text = plan.tips.localNote)
                        TipItem(label = "Corridor note", text = plan.tips.corridorNote)
                    }
                }
            }

            item { CenteredSectionDivider() }

            // 🅿️ SECTION 3: PARKING
            item {
                SectionCard(
                    title = "🅿️ Parking",
                    subtitle = "Verified car parking lots at destination"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ParkingLotCard(
                            label = "Best Option",
                            lotName = plan.parking.bestLotName,
                            type = plan.parking.bestLotType,
                            mapLink = plan.parking.bestLotMapLink,
                            onOpenMaps = { viewModel.openMaps(context, plan.parking.bestLotMapLink) }
                        )

                        ParkingLotCard(
                            label = "Backup Option",
                            lotName = plan.parking.backupLotName,
                            type = plan.parking.backupLotType,
                            mapLink = plan.parking.backupLotMapLink,
                            onOpenMaps = { viewModel.openMaps(context, plan.parking.backupLotMapLink) }
                        )
                    }
                }
            }

            item { CenteredSectionDivider() }

            // 🆘 SECTION 4: EMERGENCY
            item {
                SectionCard(
                    title = "🆘 Emergency Protocol",
                    subtitle = "One-Tap Highway Helpline & RSA Dialers"
                ) {
                    EmergencyQuickCallList(
                        rsaTitle = "24x7 Roadside Assistance",
                        rsaPhone = carDetails.oemRsaPhone
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RoadTextWithChips(road: String) {
    val tokens = road.split(" ").map { it.trim() }.filter { it.isNotBlank() }
    
    FlowRow(
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(start = 6.dp)
    ) {
        tokens.forEach { token ->
            val isHighway = token.startsWith("NH", ignoreCase = true) ||
                             token.startsWith("SH", ignoreCase = true) ||
                             token.startsWith("NE", ignoreCase = true) ||
                             token.startsWith("AH", ignoreCase = true) ||
                             token.startsWith("NICE", ignoreCase = true) ||
                             token.any { it.isDigit() }
                             
            if (isHighway) {
                Surface(
                    color = Color(0xFFF59E0B), // Vibrant yellow/orange like Google Maps highway shields
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(vertical = 1.dp)
                ) {
                    Text(
                        text = token,
                        color = Color.Black,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp
                    )
                }
            } else {
                Text(
                    text = token,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun RoutePointRow(
    label: String,
    title: String,
    road: String,
    isPassThrough: Boolean,
    isStart: Boolean = false,
    isDestination: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Circular Indicator
        Surface(
            shape = CircleShape,
            color = when {
                isStart -> NLineBluePrimary
                isDestination -> NLineRedAccent
                else -> HighwayGreen
            },
            modifier = Modifier
                .padding(top = 2.dp)
                .size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = when {
                        isStart -> "S"
                        isDestination -> "D"
                        else -> "T"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Line 1: Badge / Label
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = when {
                    isStart -> NLineBluePrimary.copy(alpha = 0.12f)
                    isDestination -> NLineRedAccent.copy(alpha = 0.12f)
                    else -> HighwayGreen.copy(alpha = 0.12f)
                },
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = when {
                        isStart -> "START (S)"
                        isDestination -> "DESTINATION (D)"
                        else -> "TOLL PIN (STOP)"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isStart -> NLineBluePrimary
                        isDestination -> NLineRedAccent
                        else -> HighwayGreen
                    },
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp
                )
            }

            // Line 2: Title / Location Name
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 6.dp, bottom = 3.dp)
            )

            // Line 3: Road/Highway Name rendered with automatic yellow Chips!
            if (road.isNotBlank()) {
                RoadTextWithChips(road = road)
            }
        }
    }
}

@Composable
private fun TipItem(label: String, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "• $label",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ParkingLotCard(
    label: String,
    lotName: String,
    type: String,
    mapLink: String,
    onOpenMaps: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenMaps() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalParking,
                contentDescription = null,
                tint = NLineBluePrimary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$label: $lotName",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Type: $type",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onOpenMaps,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Maps",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Map", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun EmergencyActionRow(label: String, action: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "• $label:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = action,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmergencyDialRow(
    label: String,
    number: String,
    subText: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "• $label: $number",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = subText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EmergencyMiniBtn(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "• $title",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CenteredSectionDivider() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider(
            modifier = Modifier
                .width(80.dp)
                .padding(vertical = 4.dp),
            thickness = 2.dp,
            color = Color.LightGray
        )
    }
}
