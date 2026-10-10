package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.SafeRoutePlan
import com.example.data.routing.HighwayCorridorEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ClaudeRouteService(
    private val geminiService: GeminiRouteService = GeminiRouteService()
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    suspend fun generateSafeRoute(
        origin: String,
        destination: String,
        routeNotes: String = "",
        algorithmPrompt: String = "",
        customApiKey: String? = null,
        customModelName: String? = null
    ): Result<SafeRoutePlan> = withContext(Dispatchers.IO) {
        val apiKey = if (!customApiKey.isNullOrBlank()) customApiKey else {
            try {
                BuildConfig.CLAUDE_API_KEY
            } catch (e: Exception) {
                null
            }
        }
        val model = if (!customModelName.isNullOrBlank()) customModelName else "claude-3-5-haiku-20241022"

        if (apiKey.isNullOrBlank() || apiKey == "MY_CLAUDE_API_KEY") {
            Log.d("ClaudeRouteService", "No Claude API key found. Delegating to HighwayCorridorEngine")
            return@withContext Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
        }

        val prompt = buildClaudeUserPrompt(origin, destination, routeNotes)
        val systemPrompt = if (algorithmPrompt.isNotBlank()) algorithmPrompt else buildClaudeSystemPrompt()

        try {
            val url = "https://api.anthropic.com/v1/messages"

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val requestJson = JSONObject().apply {
                put("model", model)
                put("max_tokens", 1500)
                put("system", systemPrompt)
                put("messages", messagesArray)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("content-type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("ClaudeRouteService", "Claude API returned code ${response.code}, falling back to deterministic engine")
                return@withContext Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }

            val responseBody = response.body?.string().orEmpty()
            val text = parseClaudeResponse(responseBody)

            if (text.isBlank()) {
                return@withContext Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }

            val parsedPlan = geminiService.parseFormattedText(text, origin, destination)
            Result.success(parsedPlan)
        } catch (e: Exception) {
            Log.e("ClaudeRouteService", "Claude call failed", e)
            Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
        }
    }

    private fun parseClaudeResponse(jsonString: String): String {
        return try {
            val root = JSONObject(jsonString)
            val contentArray = root.optJSONArray("content")
            val firstBlock = contentArray?.optJSONObject(0)
            firstBlock?.optString("text").orEmpty()
        } catch (e: Exception) {
            ""
        }
    }

    private fun buildClaudeSystemPrompt(): String {
        return """
You plan car routes for a Hyundai i20 N Line with an active Bluelink subscription, anywhere in India.
The goal is the safest good road, not the shortest. Distance and duration do not matter. A longer divided highway is better than a shorter village road, ghat shortcut, or city cut.

## Safest path road rating table:
- ★★★★★: Access-controlled expressway, open end to end, median, no village crossings. NE-series, state expressways, greenfield corridors. (Penalty: 1.0)
- ★★★★★: Access-controlled city ring road or bypass with a median. (Penalty: 1.1)
- ★★★★☆: Divided four-lane or six-lane road, median barrier, service road or flyover through every town. (Penalty: 1.2)
- ★★★☆☆: Divided road where some towns are crossed at grade. (Penalty: 2.0)
- ★★☆☆☆: Two-lane NH with paved shoulders. (Penalty: 5.0)
- ★☆☆☆☆: Undivided two-lane NH, only if no divided road exists. (Penalty: 10.0)
- ⚠️: Undivided state highway, last join only. (Penalty: 50.0)
- ⛔: Village road, market road, inner-city cut, unmarked ghat shortcut, expressway stretch not yet open. (REJECT)

How to rank:
1. List two or three real corridors between start and destination: the expressway, the divided NH, and the shortcut Maps offers. Split each into segments with road number and rating.
2. Reject any corridor with a ⛔ segment outside the exempt legs.
3. The corridor with the highest route rating wins, whatever the extra distance.
4. Tie breakers: fewest km at that weakest rating, fewest hazard points, lowest sum of km × penalty, more toll plazas.

Pin methodology:
- Plan the road first. Every segment must be an expressway, national highway, or divided bypass. Name the number.
- A pin is only a lock. It is a pass-through. The driver does not stop and does not get out.
- Every toll plaza on the planned road is a pin by default. It spans both carriageways and cannot force a U-turn.
- Every change of road number gets a pin just after the change, on the new road.
- Place the pin just after the point where Maps would turn off, on the planned road. Not before it.
- Reject a village name, area name, or locality. Reject words Junction, Exit, and Pass-through.

Link:
- Form: https://www.google.com/maps/dir/START/STOP/STOP/END/
- Plus signs between words. Nothing after the last slash. No api=1, no coordinates, no waypoints.
- Add the road to every stop.

Print strictly in this order and format:
🚗 Trip details
- Distance: about [N] km on the chosen route.
- Drive time: about [N] h without stops.
- Path: [start] → [road number via towns] → [road number] → [destination].

🗺️ Route
- [filled Google Maps URL]
- Check: preview stays on the named expressway, NH, or divided bypass
- If no directions: search the destination → Directions → set the start → Add stop for each pin
- [Start] → [NH or expressway number]
- [Stop 1 name] [Road number] → pass-through, pay and continue
- [Stop 2 name] [Road number] → pass-through, pay and continue
- [Destination]
- ✅ Road numbers used: [numbers].

🏁 Route rating
- Rating: [stars of the weakest segment]
- Selected: [corridor as road numbers and via towns]
- Weakest: [road number, stretch, km, and why]
- Rejected: [other corridors, stars, and why]

📝 Tips
- Best time: [best departure window]
- Avoid: [stretches, hours, or diversions to avoid]
- Local note: [local driving or temple/visit practice]
- Corridor note: [surface, patrol, 1033 availability]

🅿️ Parking
- Best: [lot name], [type], [https://www.google.com/maps/search/...]
- Backup: [lot name], [type], [https://www.google.com/maps/search/...]

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

    private fun buildClaudeUserPrompt(origin: String, destination: String, notes: String): String {
        return """
Plan the safe highway trip from:
Start: $origin
Destination: $destination
${if (notes.isNotBlank()) "Additional notes: $notes" else ""}
        """.trimIndent()
    }
}
