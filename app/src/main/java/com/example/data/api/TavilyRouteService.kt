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
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class TavilyRouteService(
    private val geminiService: GeminiRouteService = GeminiRouteService()
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateSafeRoute(
        origin: String,
        destination: String,
        routeNotes: String = "",
        algorithmPrompt: String = "",
        tavilyApiKey: String,
        customGeminiApiKey: String? = null,
        modelName: String? = null
    ): Result<SafeRoutePlan> = withContext(Dispatchers.IO) {
        val query = "highway road conditions traffic updates accident hazards from $origin to $destination India"
        Log.d("TavilyRouteService", "Searching Tavily for: $query")

        var searchContext = ""
        try {
            val url = "https://api.tavily.com/search"
            val requestJson = JSONObject().apply {
                put("api_key", tavilyApiKey)
                put("query", query)
                put("search_depth", "basic")
                put("include_answer", true)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string().orEmpty()
                val json = JSONObject(responseBody)
                val answer = json.optString("answer")
                val results = json.optJSONArray("results")
                
                val sb = StringBuilder()
                if (!answer.isNullOrBlank()) {
                    sb.append("Summary: ").append(answer).append("\n\n")
                }
                if (results != null) {
                    sb.append("Web Search Results:\n")
                    for (i in 0 until minOf(results.length(), 4)) {
                        val res = results.getJSONObject(i)
                        sb.append("- ").append(res.optString("title")).append(": ")
                            .append(res.optString("content")).append("\n")
                    }
                }
                searchContext = sb.toString()
                Log.d("TavilyRouteService", "Tavily search successful. Context size: ${searchContext.length}")
            } else {
                Log.w("TavilyRouteService", "Tavily API returned code ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("TavilyRouteService", "Tavily search failed", e)
        }

        // Now, append the search context to the prompt and generate the route via Gemini
        val enhancedNotes = buildString {
            if (routeNotes.isNotBlank()) append(routeNotes).append("\n\n")
            if (searchContext.isNotBlank()) {
                append("### LATEST REAL-TIME ROAD CONDITIONS SEARCHED VIA TAVILY:\n")
                append(searchContext).append("\n")
                append("Please adjust the rating and tips based on the latest hazards or road closures listed above if applicable.")
            }
        }

        // Use the custom Gemini API key or fall back to BuildConfig.GEMINI_API_KEY
        val geminiKey = if (!customGeminiApiKey.isNullOrBlank()) customGeminiApiKey else {
            try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Exception) {
                null
            }
        }

        geminiService.generateSafeRoute(
            origin = origin,
            destination = destination,
            routeNotes = enhancedNotes,
            algorithmPrompt = algorithmPrompt,
            customApiKey = geminiKey,
            customModelName = modelName
        )
    }
}
