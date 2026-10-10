package com.example.data.firebase

import android.util.Log
import com.example.data.model.RouteEmergency
import com.example.data.model.RouteParking
import com.example.data.model.RouteStop
import com.example.data.model.RouteTips
import com.example.data.model.SafeRoutePlan
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

data class CloudParkingPin(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val locationType: String = "Secure Covered Parking",
    val mapLink: String = "",
    val verifiedBy: String = "",
    val carSuitability: String = "Hyundai i20 N Line / Sedans / SUVs",
    val timestamp: Long = System.currentTimeMillis()
)

data class CloudRoutingConfig(
    val apiKey: String = "",
    val apiType: String = "GEMINI", // "GEMINI", "CLAUDE", "SILICONFLOW", or "TAVILY"
    val modelName: String = "",
    val prompt: String = "",
    val geminiApiKey: String = ""
)

object FirestoreService {
    private const val TAG = "FirestoreService"

    private var customFirestore: FirebaseFirestore? = null

    private val firestore: FirebaseFirestore?
        get() = customFirestore

    fun init(context: android.content.Context) {
        if (customFirestore != null) return
        try {
            val dbId = context.getString(com.example.R.string.firestore_database_id).trim()
            if (dbId.isBlank() || dbId == "(default)") {
                customFirestore = FirebaseFirestore.getInstance()
                Log.d(TAG, "Initialized Firestore with standard default database instance")
            } else {
                customFirestore = FirebaseFirestore.getInstance(dbId)
                Log.d(TAG, "Initialized Firestore with custom database ID: $dbId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firestore with custom database ID", e)
            try {
                customFirestore = FirebaseFirestore.getInstance()
                Log.d(TAG, "Fell back to default Firestore instance")
            } catch (ex: Exception) {
                Log.e(TAG, "Failed to get default Firestore instance", ex)
            }
        }
    }

    fun isConfigured(): Boolean {
        return try {
            FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun saveRouteToCloud(userEmail: String, plan: SafeRoutePlan): Boolean {
        val db = firestore ?: return false
        val cleanEmail = userEmail.ifBlank { "anonymous" }.replace(".", "_").replace("@", "_at_")
        return try {
            val routeDoc = hashMapOf(
                "id" to plan.id,
                "title" to plan.title,
                "origin" to plan.origin,
                "destination" to plan.destination,
                "distanceApprox" to plan.distanceApprox,
                "primaryRoads" to plan.primaryRoads,
                "mapsUrl" to plan.mapsUrl,
                "verifyLine" to plan.verifyLine,
                "bestTime" to plan.tips.bestTime,
                "avoid" to plan.tips.avoid,
                "corridorNote" to plan.tips.corridorNote,
                "bestLotName" to plan.parking.bestLotName,
                "bestLotMapLink" to plan.parking.bestLotMapLink,
                "backupLotName" to plan.parking.backupLotName,
                "backupLotMapLink" to plan.parking.backupLotMapLink,
                "hyundaiRsaPhone" to plan.emergency.hyundaiRsaPhone,
                "nhaiEmergency" to plan.emergency.nhaiHighwayEmergency,
                "userEmail" to userEmail,
                "carModel" to plan.carModel,
                "updatedAt" to System.currentTimeMillis()
            )

            withTimeoutOrNull(4000) {
                db.collection("users")
                    .document(cleanEmail)
                    .collection("route_history")
                    .document(plan.id.ifBlank { "route_${System.currentTimeMillis()}" })
                    .set(routeDoc, SetOptions.merge())
                    .await()
            } != null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save route to Firestore: ${e.message}")
            false
        }
    }

    suspend fun fetchCloudRoutes(userEmail: String): List<SafeRoutePlan> {
        val db = firestore ?: return emptyList()
        val cleanEmail = userEmail.ifBlank { "anonymous" }.replace(".", "_").replace("@", "_at_")
        return try {
            val result = withTimeoutOrNull(4000) {
                db.collection("users")
                    .document(cleanEmail)
                    .collection("route_history")
                    .get()
                    .await()
            } ?: return emptyList()

            result.documents.mapNotNull { doc ->
                val id = doc.getString("id") ?: doc.id
                val title = doc.getString("title") ?: return@mapNotNull null
                val origin = doc.getString("origin") ?: ""
                val dest = doc.getString("destination") ?: ""
                @Suppress("UNCHECKED_CAST")
                val roads = doc.get("primaryRoads") as? List<String> ?: listOf("NH Highway")
                val mapsUrl = doc.getString("mapsUrl") ?: ""
                val verifyLine = doc.getString("verifyLine") ?: "Audited Highway Corridor"

                SafeRoutePlan(
                    id = id,
                    title = title,
                    origin = origin,
                    destination = dest,
                    distanceApprox = doc.getString("distanceApprox") ?: "300 km",
                    primaryRoads = roads,
                    mapsUrl = mapsUrl,
                    stops = listOf(
                        RouteStop(
                            placeName = "Toll Corridor",
                            roadNumber = roads.firstOrNull() ?: "NH",
                            isPassThrough = true,
                            note = "→ Verified Cloud Sync"
                        )
                    ),
                    verifyLine = verifyLine,
                    tips = RouteTips(
                        bestTime = doc.getString("bestTime") ?: "Daylight hours (06:00 - 18:00)",
                        avoid = doc.getString("avoid") ?: "Avoid unlit interior bypasses after dark",
                        localNote = "Synced from Cloud Firestore",
                        corridorNote = doc.getString("corridorNote") ?: "Divided multi-lane highway"
                    ),
                    parking = RouteParking(
                        bestLotName = doc.getString("bestLotName") ?: "Secure Verified Parking",
                        bestLotType = "Covered / Security Guard",
                        bestLotMapLink = doc.getString("bestLotMapLink") ?: "",
                        backupLotName = doc.getString("backupLotName") ?: "Commercial Mall Basement",
                        backupLotType = "Multi-level Covered",
                        backupLotMapLink = doc.getString("backupLotMapLink") ?: ""
                    ),
                    emergency = RouteEmergency(
                        hyundaiRsaPhone = doc.getString("hyundaiRsaPhone") ?: "1800 102 4645",
                        nhaiHighwayEmergency = doc.getString("nhaiEmergency") ?: "1033"
                    ),
                    isCurated = false,
                    userEmail = userEmail,
                    carModel = doc.getString("carModel") ?: "Hyundai i20 N Line"
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch routes from Firestore: ${e.message}")
            emptyList()
        }
    }

    suspend fun saveParkingPin(pin: CloudParkingPin): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = pin.id.ifBlank { "pin_${System.currentTimeMillis()}" }
            withTimeoutOrNull(4000) {
                db.collection("parking_pins")
                    .document(docId)
                    .set(pin, SetOptions.merge())
                    .await()
            } != null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save parking pin: ${e.message}")
            false
        }
    }

    suspend fun fetchRoutingPrompt(): String? {
        val db = firestore ?: return null
        return try {
            withTimeoutOrNull(4000) {
                val doc = db.collection("config")
                    .document("routing")
                    .get()
                    .await()
                doc.getString("prompt")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch routing prompt from Firestore: ${e.message}")
            null
        }
    }

    suspend fun fetchRoutingConfig(docName: String = "gemini"): CloudRoutingConfig? {
        val db = firestore ?: return null
        return try {
            withTimeoutOrNull(4000) {
                val doc = db.collection("config")
                    .document(docName)
                    .get()
                    .await()
                if (doc.exists()) {
                    CloudRoutingConfig(
                        apiKey = doc.getString("apiKey").orEmpty(),
                        apiType = doc.getString("apiType").orEmpty().uppercase(),
                        modelName = doc.getString("modelName").orEmpty(),
                        prompt = doc.getString("prompt").orEmpty(),
                        geminiApiKey = doc.getString("geminiApiKey").orEmpty()
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch routing config: ${e.message}")
            null
        }
    }

    suspend fun saveRoutingConfig(config: CloudRoutingConfig): Boolean {
        val db = firestore ?: return false
        return try {
            withTimeoutOrNull(4000) {
                db.collection("config")
                    .document("gemini")
                    .set(config, SetOptions.merge())
                    .await()
            } != null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save routing config: ${e.message}")
            false
        }
    }

    suspend fun saveVehicleToCloud(userEmail: String, vehicle: com.example.data.local.VehicleEntity): Boolean {
        val db = firestore ?: return false
        val cleanEmail = userEmail.ifBlank { "anonymous" }.replace(".", "_").replace("@", "_at_")
        return try {
            val vehicleDoc = hashMapOf(
                "registrationNumber" to vehicle.registrationNumber,
                "makeModel" to vehicle.makeModel,
                "variant" to vehicle.variant,
                "tyreSpecification" to vehicle.tyreSpecification,
                "recommendedPsi" to vehicle.recommendedPsi,
                "hasConnectedCarBluelink" to vehicle.hasConnectedCarBluelink,
                "fastagProvider" to vehicle.fastagProvider,
                "fuelType" to vehicle.fuelType,
                "oemRsaPhone" to vehicle.oemRsaPhone,
                "sosButtonInstruction" to vehicle.sosButtonInstruction,
                "userEmail" to userEmail,
                "isDefault" to vehicle.isDefault,
                "updatedAt" to System.currentTimeMillis()
            )

            withTimeoutOrNull(4000) {
                db.collection("users")
                    .document(cleanEmail)
                    .collection("vehicles")
                    .document(vehicle.registrationNumber)
                    .set(vehicleDoc, SetOptions.merge())
                    .await()
            } != null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to save vehicle to Firestore: ${e.message}")
            false
        }
    }

    suspend fun deleteVehicleFromCloud(userEmail: String, registrationNumber: String): Boolean {
        val db = firestore ?: return false
        val cleanEmail = userEmail.ifBlank { "anonymous" }.replace(".", "_").replace("@", "_at_")
        return try {
            withTimeoutOrNull(4000) {
                db.collection("users")
                    .document(cleanEmail)
                    .collection("vehicles")
                    .document(registrationNumber)
                    .delete()
                    .await()
            } != null
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete vehicle from Firestore: ${e.message}")
            false
        }
    }

    suspend fun fetchCloudVehicles(userEmail: String): List<com.example.data.local.VehicleEntity> {
        val db = firestore ?: return emptyList()
        val cleanEmail = userEmail.ifBlank { "anonymous" }.replace(".", "_").replace("@", "_at_")
        return try {
            val result = withTimeoutOrNull(4000) {
                db.collection("users")
                    .document(cleanEmail)
                    .collection("vehicles")
                    .get()
                    .await()
            } ?: return emptyList()

            result.documents.mapNotNull { doc ->
                val reg = doc.getString("registrationNumber") ?: doc.id
                val makeModel = doc.getString("makeModel") ?: return@mapNotNull null
                val variant = doc.getString("variant") ?: ""
                val tyres = doc.getString("tyreSpecification") ?: ""
                val psi = doc.getString("recommendedPsi") ?: ""
                val bluelink = doc.getBoolean("hasConnectedCarBluelink") ?: false
                val fastag = doc.getString("fastagProvider") ?: ""
                val fuel = doc.getString("fuelType") ?: ""
                val rsa = doc.getString("oemRsaPhone") ?: ""
                val sos = doc.getString("sosButtonInstruction") ?: ""
                val isDefault = doc.getBoolean("isDefault") ?: false

                com.example.data.local.VehicleEntity(
                    registrationNumber = reg,
                    makeModel = makeModel,
                    variant = variant,
                    tyreSpecification = tyres,
                    recommendedPsi = psi,
                    hasConnectedCarBluelink = bluelink,
                    fastagProvider = fastag,
                    fuelType = fuel,
                    oemRsaPhone = rsa,
                    sosButtonInstruction = sos,
                    userEmail = userEmail,
                    isDefault = isDefault
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch vehicles from Firestore: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchActiveEngineName(): String {
        val db = firestore ?: return "OPENROUTER"
        return try {
            withTimeoutOrNull(4000) {
                val doc = db.collection("config")
                    .document("routing_control")
                    .get()
                    .await()
                doc.getString("activeEngine")?.uppercase()?.trim() ?: "OPENROUTER"
            } ?: "OPENROUTER"
        } catch (e: Exception) {
            Log.w(TAG, "Failed to fetch active engine name, falling back to OPENROUTER: ${e.message}")
            "OPENROUTER"
        }
    }

    suspend fun initializeTavilyConfigInCloud(): Boolean {
        val db = firestore ?: return false
        return try {
            // 1. Seed active engine selection document (routing_control)
            val routingControlRef = db.collection("config").document("routing_control")
            val controlDoc = routingControlRef.get().await()
            if (!controlDoc.exists() || controlDoc.getString("activeEngine").isNullOrBlank()) {
                routingControlRef.set(hashMapOf("activeEngine" to "OPENROUTER"), SetOptions.merge()).await()
            }

            // 2. Seed openrouter config
            val openrouterRef = db.collection("config").document("openrouter")
            val orDoc = openrouterRef.get().await()
            if (!orDoc.exists() || orDoc.getString("apiKey").isNullOrBlank()) {
                val config = hashMapOf(
                    "apiKey" to "",
                    "apiType" to "OPENROUTER",
                    "modelName" to "openai/gpt-4o",
                    "prompt" to "",
                    "geminiApiKey" to ""
                )
                openrouterRef.set(config, SetOptions.merge()).await()
            }

            // 3. Seed tavily config
            val tavilyRef = db.collection("config").document("tavily")
            val tvDoc = tavilyRef.get().await()
            if (!tvDoc.exists() || tvDoc.getString("apiKey").isNullOrBlank()) {
                val config = hashMapOf(
                    "apiKey" to "",
                    "apiType" to "TAVILY",
                    "modelName" to "gemini-3.5-flash",
                    "prompt" to "",
                    "geminiApiKey" to ""
                )
                tavilyRef.set(config, SetOptions.merge()).await()
            }

            // 4. Seed gemini config placeholder
            val geminiRef = db.collection("config").document("gemini")
            val gemDoc = geminiRef.get().await()
            if (!gemDoc.exists()) {
                val config = hashMapOf(
                    "apiKey" to "",
                    "apiType" to "GEMINI",
                    "modelName" to "gemini-3.5-flash",
                    "prompt" to "",
                    "geminiApiKey" to ""
                )
                geminiRef.set(config, SetOptions.merge()).await()
            }

            // 5. Seed claude config placeholder
            val claudeRef = db.collection("config").document("claude")
            val clDoc = claudeRef.get().await()
            if (!clDoc.exists()) {
                val config = hashMapOf(
                    "apiKey" to "",
                    "apiType" to "CLAUDE",
                    "modelName" to "claude-3-5-haiku-20241022",
                    "prompt" to "",
                    "geminiApiKey" to ""
                )
                claudeRef.set(config, SetOptions.merge()).await()
            }

            // 6. Seed siliconflow config placeholder
            val siliconflowRef = db.collection("config").document("siliconflow")
            val sfDoc = siliconflowRef.get().await()
            if (!sfDoc.exists()) {
                val config = hashMapOf(
                    "apiKey" to "",
                    "apiType" to "SILICONFLOW",
                    "modelName" to "deepseek-ai/DeepSeek-V3",
                    "prompt" to "",
                    "geminiApiKey" to ""
                )
                siliconflowRef.set(config, SetOptions.merge()).await()
            }

            Log.d(TAG, "Successfully seeded multi-engine Firestore configurations!")
            true
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize/seed dynamic configurations in cloud: ${e.message}")
            false
        }
    }
}
