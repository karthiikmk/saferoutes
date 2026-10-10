package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class PlaceSuggestion(
    val title: String,
    val subtitle: String,
    val fullFormatted: String,
    val isHighwayVerified: Boolean = true
)

object PlaceAutocompleteService {

    // Pre-indexed database of prominent Indian highway hubs, toll junctions, tech clusters, and cities
    private val popularIndianHighwayHubs = listOf(
        // Tamil Nadu / Chennai corridors
        PlaceSuggestion("Adyar, Chennai", "Tamil Nadu • Outer Ring Road Corridor", "Adyar, Chennai, Tamil Nadu"),
        PlaceSuggestion("Guindy, Chennai", "Tamil Nadu • Kathipara Flyover Junction", "Guindy, Chennai, Tamil Nadu"),
        PlaceSuggestion("Tambaram, Chennai", "Tamil Nadu • NH45 GST Road Gateway", "Tambaram, Chennai, Tamil Nadu"),
        PlaceSuggestion("Vandalur, Chennai", "Tamil Nadu • Chennai ORR Toll Plaza", "Vandalur, Chennai, Tamil Nadu"),
        PlaceSuggestion("Sriperumbudur, Chennai", "Tamil Nadu • NH48 Bengaluru Highway", "Sriperumbudur, Tamil Nadu"),
        PlaceSuggestion("Kanchipuram, Tamil Nadu", "Tamil Nadu • NH48 Interchange", "Kanchipuram, Tamil Nadu"),
        PlaceSuggestion("Vellore, Tamil Nadu", "Tamil Nadu • Pallikonda Toll Plaza", "Vellore, Tamil Nadu"),
        PlaceSuggestion("Coimbatore, Tamil Nadu", "Tamil Nadu • Avinashi NH544 Bypass", "Coimbatore, Tamil Nadu"),
        PlaceSuggestion("Madurai, Tamil Nadu", "Tamil Nadu • Ring Road Kappalur", "Madurai, Tamil Nadu"),
        PlaceSuggestion("Salem, Tamil Nadu", "Tamil Nadu • NH44 Junction", "Salem, Tamil Nadu"),
        PlaceSuggestion("Tiruchirappalli (Trichy)", "Tamil Nadu • NH45 Samayapuram", "Tiruchirappalli, Tamil Nadu"),

        // Karnataka / Bengaluru corridors
        PlaceSuggestion("Indiranagar, Bengaluru", "Karnataka • Old Airport Road", "Indiranagar, Bengaluru, Karnataka"),
        PlaceSuggestion("Electronic City, Bengaluru", "Karnataka • Elevated Expressway Toll", "Electronic City, Bengaluru, Karnataka"),
        PlaceSuggestion("Whitefield, Bengaluru", "Karnataka • ITPL Corridor", "Whitefield, Bengaluru, Karnataka"),
        PlaceSuggestion("Hebbal, Bengaluru", "Karnataka • Airport Elevated Highway", "Hebbal, Bengaluru, Karnataka"),
        PlaceSuggestion("Hosur Road, Bengaluru", "Karnataka / Tamil Nadu Border • NH44 Attibele", "Hosur Road, Bengaluru, Karnataka"),
        PlaceSuggestion("Mysuru (Mysore)", "Karnataka • Bengaluru-Mysuru Expressway", "Mysuru, Karnataka"),
        PlaceSuggestion("Mangaluru (Mangalore)", "Karnataka • Shiradi Ghat NH75", "Mangaluru, Karnataka"),
        PlaceSuggestion("Hubballi - Dharwad", "Karnataka • Pune-Bengaluru NH48", "Hubballi, Karnataka"),

        // Maharashtra / Mumbai-Pune corridors
        PlaceSuggestion("Bandra West, Mumbai", "Maharashtra • Western Express Highway", "Bandra West, Mumbai, Maharashtra"),
        PlaceSuggestion("Navi Mumbai, Mumbai", "Maharashtra • Palm Beach Road / Sion-Panvel", "Navi Mumbai, Maharashtra"),
        PlaceSuggestion("Hinjawadi, Pune", "Maharashtra • Mumbai-Pune Expressway End", "Hinjawadi, Pune, Maharashtra"),
        PlaceSuggestion("Koregaon Park, Pune", "Maharashtra • Pune Nagar Road", "Koregaon Park, Pune, Maharashtra"),
        PlaceSuggestion("Thane, Maharashtra", "Maharashtra • Eastern Express Highway", "Thane, Maharashtra"),
        PlaceSuggestion("Nashik, Maharashtra", "Maharashtra • Samruddhi Mahamarg Corridor", "Nashik, Maharashtra"),
        PlaceSuggestion("Nagpur, Maharashtra", "Maharashtra • Samruddhi Expressway Junction", "Nagpur, Maharashtra"),

        // Goa corridors
        PlaceSuggestion("Panaji, Goa", "Goa • Mandovi River / NH66", "Panaji, Goa"),
        PlaceSuggestion("Candolim, Goa", "North Goa • Coastal Highway", "Candolim, Goa"),
        PlaceSuggestion("Margao, Goa", "South Goa • NH66 Bypass", "Margao, Goa"),

        // Telangana & Andhra Pradesh corridors
        PlaceSuggestion("Gachibowli, Hyderabad", "Telangana • Nehru Outer Ring Road (ORR)", "Gachibowli, Hyderabad, Telangana"),
        PlaceSuggestion("HITEC City, Hyderabad", "Telangana • Cyberabad Corridor", "HITEC City, Hyderabad, Telangana"),
        PlaceSuggestion("Shamshabad, Hyderabad", "Telangana • Airport NH44 Expressway", "Shamshabad, Hyderabad, Telangana"),
        PlaceSuggestion("Vijayawada, Andhra Pradesh", "Andhra Pradesh • NH16 Corridor", "Vijayawada, Andhra Pradesh"),
        PlaceSuggestion("Visakhapatnam (Vizag)", "Andhra Pradesh • NH16 Coastal Highway", "Visakhapatnam, Andhra Pradesh"),
        PlaceSuggestion("Tirupati, Andhra Pradesh", "Andhra Pradesh • Alipiri / Karakambadi", "Tirupati, Andhra Pradesh"),

        // Delhi NCR & North corridors
        PlaceSuggestion("Connaught Place, New Delhi", "Delhi • Central Capital Hub", "Connaught Place, New Delhi"),
        PlaceSuggestion("Aerocity, New Delhi", "Delhi • IGI Airport Corridor", "Aerocity, New Delhi"),
        PlaceSuggestion("Gurugram (Gurgaon)", "Haryana • Delhi-Jaipur NH48 Expressway", "Gurugram, Haryana"),
        PlaceSuggestion("Noida, Uttar Pradesh", "Uttar Pradesh • Yamuna Expressway Gateway", "Noida, Uttar Pradesh"),
        PlaceSuggestion("Greater Noida", "Uttar Pradesh • Eastern Peripheral Expressway", "Greater Noida, Uttar Pradesh"),
        PlaceSuggestion("Agra, Uttar Pradesh", "Uttar Pradesh • Yamuna / Agra-Lucknow Expressway", "Agra, Uttar Pradesh"),
        PlaceSuggestion("Jaipur, Rajasthan", "Rajasthan • Delhi-Mumbai Expressway Interchange", "Jaipur, Rajasthan"),
        PlaceSuggestion("Lucknow, Uttar Pradesh", "Uttar Pradesh • Purvanchal Expressway", "Lucknow, Uttar Pradesh"),
        PlaceSuggestion("Chandigarh", "Punjab / Haryana • Himalayan Expressway Bypass", "Chandigarh"),

        // Gujarat corridors
        PlaceSuggestion("Ahmedabad, Gujarat", "Gujarat • NE-1 Ahmedabad-Vadodara Expressway", "Ahmedabad, Gujarat"),
        PlaceSuggestion("Vadodara, Gujarat", "Gujarat • NH48 Golden Quadrilateral", "Vadodara, Gujarat"),
        PlaceSuggestion("Surat, Gujarat", "Gujarat • Hazira Expressway Bypass", "Surat, Gujarat"),

        // Kerala corridors
        PlaceSuggestion("Kochi (Cochin)", "Kerala • NH66 / Edappally Bypass", "Kochi, Kerala"),
        PlaceSuggestion("Thiruvananthapuram (Trivandrum)", "Kerala • Kazhakkoottam NH66", "Thiruvananthapuram, Kerala"),
        PlaceSuggestion("Kozhikode (Calicut)", "Kerala • NH66 Bypass", "Kozhikode, Kerala")
    )

    /**
     * Search places from map database using free APIs.
     * If query is empty, provides top popular starting points so user can select immediately.
     */
    suspend fun searchPlaces(
        query: String,
        googleApiKeyOverride: String = ""
    ): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
        val trimmed = query.trim()

        // When empty or 1 char, provide top highway cities as quick picks
        if (trimmed.isEmpty()) {
            return@withContext listOf(
                popularIndianHighwayHubs[0], // Adyar, Chennai
                popularIndianHighwayHubs[11], // Indiranagar, Bengaluru
                popularIndianHighwayHubs[12], // Electronic City, Bengaluru
                popularIndianHighwayHubs[18], // Bandra West, Mumbai
                popularIndianHighwayHubs[20], // Hinjawadi, Pune
                popularIndianHighwayHubs[27]  // Gachibowli, Hyderabad
            )
        }

        // 1. Instant local matches (matching place, city, or state)
        val localMatches = popularIndianHighwayHubs.filter { hub ->
            hub.title.contains(trimmed, ignoreCase = true) ||
            hub.subtitle.contains(trimmed, ignoreCase = true) ||
            hub.fullFormatted.contains(trimmed, ignoreCase = true)
        }.take(5)

        // 2. Query Google Places API (if user provided an API key in Settings)
        val googleMatches = mutableListOf<PlaceSuggestion>()
        if (googleApiKeyOverride.isNotBlank()) {
            try {
                val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
                val url = URL("https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$encodedQuery&components=country:in&key=$googleApiKeyOverride")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 2000
                conn.readTimeout = 2000
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val root = JSONObject(body)
                    val predictions = root.optJSONArray("predictions")
                    if (predictions != null) {
                        for (i in 0 until predictions.length()) {
                            val p = predictions.optJSONObject(i) ?: continue
                            val desc = p.optString("description", "")
                            val structured = p.optJSONObject("structured_formatting")
                            val mainText = structured?.optString("main_text", desc) ?: desc
                            val secondaryText = structured?.optString("secondary_text", "India") ?: "India"
                            if (desc.isNotBlank()) {
                                googleMatches.add(
                                    PlaceSuggestion(
                                        title = mainText,
                                        subtitle = "$secondaryText • Google Places",
                                        fullFormatted = desc,
                                        isHighwayVerified = true
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to free API seamlessly
            }
        }

        // 3. Query free OpenStreetMap Photon Geocoding API (Zero cost, no API key required)
        val onlineMatches = mutableListOf<PlaceSuggestion>()
        try {
            val encodedQuery = URLEncoder.encode("$trimmed India", "UTF-8")
            val url = URL("https://photon.komoot.io/api/?q=$encodedQuery&limit=6&lang=en")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 1800
            connection.readTimeout = 1800
            connection.setRequestProperty("User-Agent", "SafeTrip-Android-FreeMapAPI")

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(responseText)
                val features = root.optJSONArray("features")
                if (features != null) {
                    for (i in 0 until features.length()) {
                        val feature = features.optJSONObject(i) ?: continue
                        val properties = feature.optJSONObject("properties") ?: continue
                        val name = properties.optString("name", "")
                        val city = properties.optString("city", properties.optString("district", ""))
                        val state = properties.optString("state", "")
                        val country = properties.optString("country", "India")

                        if (name.isNotBlank()) {
                            val title = if (city.isNotBlank() && !name.equals(city, ignoreCase = true)) {
                                "$name, $city"
                            } else {
                                name
                            }
                            val subtitle = listOf(state, country).filter { it.isNotBlank() }.joinToString(", ")
                            val full = if (subtitle.isNotBlank()) "$title, $subtitle" else title

                            // Avoid exact duplicates with local matches
                            if (localMatches.none { it.title.equals(title, ignoreCase = true) }) {
                                onlineMatches.add(
                                    PlaceSuggestion(
                                        title = title,
                                        subtitle = "$subtitle • Open Places Map",
                                        fullFormatted = full,
                                        isHighwayVerified = true
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Gracefully ignore network timeout and rely on local database
        }

        (googleMatches + localMatches + onlineMatches).distinctBy { it.fullFormatted }.take(6)
    }
}
