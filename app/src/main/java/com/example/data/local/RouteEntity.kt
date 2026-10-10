package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class RouteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val origin: String,
    val destination: String,
    val distanceApprox: String,
    val mapsUrl: String,
    val primaryRoads: String, // comma-separated
    val verifyLine: String,
    val bestTime: String,
    val avoid: String,
    val localNote: String,
    val corridorNote: String,
    val bestLotName: String,
    val bestLotType: String,
    val bestLotMapLink: String,
    val backupLotName: String,
    val backupLotType: String,
    val backupLotMapLink: String,
    val formattedOutput: String,
    val isFavorite: Boolean = false,
    val userEmail: String = "",
    val carModel: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
