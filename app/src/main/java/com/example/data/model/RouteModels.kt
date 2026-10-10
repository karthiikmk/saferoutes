package com.example.data.model

data class RouteStop(
    val placeName: String,
    val roadNumber: String,
    val isPassThrough: Boolean = true,
    val note: String = "→ pass-through, pay and continue"
)

data class RouteTips(
    val bestTime: String,
    val avoid: String,
    val localNote: String,
    val corridorNote: String
)

data class RouteParking(
    val bestLotName: String,
    val bestLotType: String,
    val bestLotMapLink: String,
    val backupLotName: String,
    val backupLotType: String,
    val backupLotMapLink: String
)

data class RouteEmergency(
    val rsaMirrorButton: String = "RSA button on the overhead console (Car, breakdown, flat tyre, battery, or no fuel)",
    val sosMirrorButton: String = "SOS button on the overhead console (Car, accident or injury)",
    val hyundaiRsaPhone: String = "1800 102 4645",
    val nationalEmergency: String = "112",
    val police: String = "100",
    val ambulance: String = "108",
    val fire: String = "101",
    val nhaiHighwayEmergency: String = "1033"
)

data class SafeRoutePlan(
    val id: String,
    val title: String,
    val origin: String,
    val destination: String,
    val distanceApprox: String,
    val primaryRoads: List<String>,
    val mapsUrl: String,
    val stops: List<RouteStop>,
    val verifyLine: String,
    val tips: RouteTips,
    val parking: RouteParking,
    val emergency: RouteEmergency = RouteEmergency(),
    val isCurated: Boolean = true,
    val userEmail: String = "",
    val carModel: String = ""
) {
    fun toFormattedPromptOutput(): String {
        val stopsBuilder = StringBuilder()
        stopsBuilder.append("- $origin → ${primaryRoads.firstOrNull() ?: "NH"}\n")
        stops.forEach { stop ->
            if (stop.isPassThrough) {
                stopsBuilder.append("- ${stop.placeName} ${stop.roadNumber} ${stop.note}\n")
            } else {
                stopsBuilder.append("- ${stop.placeName} → ${stop.roadNumber}\n")
            }
        }
        stopsBuilder.append("- $destination\n")

        return """
🗺️ Route
- $mapsUrl
- Check: preview stays on the named expressway, NH, or divided bypass
- If no directions: search the destination → Directions → set the start → Add stop for each pin
${stopsBuilder.toString().trimEnd()}
- $verifyLine

📝 Tips
- Best time: ${tips.bestTime}
- Avoid: ${tips.avoid}
- Local note: ${tips.localNote}
- Corridor note: ${tips.corridorNote}

🅿️ Parking
- Best: ${parking.bestLotName}, ${parking.bestLotType}, ${parking.bestLotMapLink}
- Backup: ${parking.backupLotName}, ${parking.backupLotType}, ${parking.backupLotMapLink}

🆘 Emergency
- Car, breakdown, flat tyre, battery, or no fuel: RSA button on the overhead console
- Car, accident or injury: SOS button on the overhead console
- Hyundai tow or fuel: 1800 102 4645
- Any emergency: 112
- Police: 100
- Ambulance: 108
- Fire: 101
- Highway blocked or crash: 1033
        """.trimIndent()
    }
}
