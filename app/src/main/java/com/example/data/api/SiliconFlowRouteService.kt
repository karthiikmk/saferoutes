package com.example.data.api

import android.util.Log
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

class SiliconFlowRouteService(
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
        apiKey: String,
        modelName: String = "deepseek-ai/DeepSeek-V3"
    ): Result<SafeRoutePlan> = withContext(Dispatchers.IO) {
        val model = if (modelName.isNotBlank()) modelName else "deepseek-ai/DeepSeek-V3"
        val systemPrompt = if (algorithmPrompt.isNotBlank()) algorithmPrompt else {
            try {
                // Read local route.md asset as prompt fallback if empty
                val context = com.google.firebase.FirebaseApp.getInstance().applicationContext
                context.assets.open("route.md").bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                "You plan car routes for safe highway trips in India. The goal is the safest road, not the shortest."
            }
        }

        val userPrompt = """
            Plan the safe highway trip from:
            Start: $origin
            Destination: $destination
            ${if (routeNotes.isNotBlank()) "Additional notes: $routeNotes" else ""}
        """.trimIndent()

        try {
            val url = "https://api.siliconflow.cn/v1/chat/completions"

            val messagesArray = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userPrompt)
                })
            }

            val requestJson = JSONObject().apply {
                put("model", model)
                put("messages", messagesArray)
                put("temperature", 0.2)
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("SiliconFlowRoute", "SiliconFlow API returned code ${response.code}")
                return@withContext Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }

            val responseBody = response.body?.string().orEmpty()
            val text = parseResponse(responseBody)

            if (text.isBlank()) {
                return@withContext Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }

            val parsedPlan = geminiService.parseFormattedText(text, origin, destination)
            Result.success(parsedPlan)
        } catch (e: Exception) {
            Log.e("SiliconFlowRoute", "SiliconFlow call failed", e)
            Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
        }
    }

    private fun parseResponse(jsonString: String): String {
        return try {
            val root = JSONObject(jsonString)
            val choices = root.optJSONArray("choices")
            val firstChoice = choices?.optJSONObject(0)
            val message = firstChoice?.optJSONObject("message")
            message?.optString("content").orEmpty()
        } catch (e: Exception) {
            ""
        }
    }
}
