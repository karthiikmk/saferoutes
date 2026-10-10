package com.example.data.repository

import com.example.data.api.ClaudeRouteService
import com.example.data.api.GeminiRouteService
import com.example.data.curated.CuratedRoutes
import com.example.data.local.RouteDao
import com.example.data.local.RouteEntity
import com.example.data.model.RouteParking
import com.example.data.model.RouteStop
import com.example.data.model.RouteTips
import com.example.data.model.SafeRoutePlan
import com.example.data.model.CarDetails
import com.example.data.routing.HighwayCorridorEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class RoutingEngineMode(val label: String) {
    AUTO("Auto (AI / Offline)"),
    DETERMINISTIC("Highway Engine (Offline / Zero AI)"),
    GEMINI("Google Gemini"),
    CLAUDE("Anthropic Claude"),
    SILICONFLOW("SiliconFlow DeepSeek")
}

class RouteRepository(
    private val routeDao: RouteDao,
    private val vehicleDao: com.example.data.local.VehicleDao? = null,
    private val geminiService: GeminiRouteService = GeminiRouteService(),
    private val claudeService: ClaudeRouteService = ClaudeRouteService(),
    private val siliconFlowService: com.example.data.api.SiliconFlowRouteService = com.example.data.api.SiliconFlowRouteService(),
    private val tavilyService: com.example.data.api.TavilyRouteService = com.example.data.api.TavilyRouteService(),
    private val openRouterService: com.example.data.api.OpenRouterRouteService = com.example.data.api.OpenRouterRouteService()
) {
    val savedRoutes: Flow<List<SafeRoutePlan>> = routeDao.getAllSavedRoutes().map { entities ->
        entities.map { it.toSafeRoutePlan() }
    }

    val favoriteRoutes: Flow<List<SafeRoutePlan>> = routeDao.getFavoriteRoutes().map { entities ->
        entities.map { it.toSafeRoutePlan() }
    }

    fun getSavedRoutesForUser(email: String): Flow<List<SafeRoutePlan>> =
        routeDao.getSavedRoutesForUser(email).map { entities ->
            entities.map { it.toSafeRoutePlan() }
        }

    fun getCuratedRoutes(): List<SafeRoutePlan> = CuratedRoutes.routes

    fun getCuratedRouteById(id: String): SafeRoutePlan? {
        return CuratedRoutes.routes.find { it.id == id }
    }

    suspend fun getSavedRouteById(id: String): SafeRoutePlan? {
        return routeDao.getRouteById(id)?.toSafeRoutePlan()
    }

    suspend fun saveRoute(plan: SafeRoutePlan, userEmail: String = "", carModel: String = ""): String {
        val finalId = if (plan.id.isBlank() || plan.id == "0") "route_${System.currentTimeMillis()}" else plan.id
        val updatedPlan = plan.copy(
            id = finalId,
            userEmail = if (userEmail.isNotBlank()) userEmail else plan.userEmail,
            carModel = if (carModel.isNotBlank()) carModel else plan.carModel
        )
        routeDao.insertRoute(updatedPlan.toEntity())
        try {
            com.example.data.firebase.FirestoreService.saveRouteToCloud(updatedPlan.userEmail, updatedPlan)
        } catch (_: Exception) {
            // Graceful fallback to offline local Room storage
        }
        return finalId
    }

    suspend fun syncRoutesWithCloud(userEmail: String) {
        if (userEmail.isBlank()) return
        try {
            val cloudRoutes = com.example.data.firebase.FirestoreService.fetchCloudRoutes(userEmail)
            cloudRoutes.forEach { cloudPlan ->
                routeDao.insertRoute(cloudPlan.toEntity())
            }
        } catch (_: Exception) {
            // Graceful offline fallback
        }
    }

    suspend fun deleteRoute(id: String) {
        routeDao.deleteRouteById(id)
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        routeDao.updateFavorite(id, isFavorite)
    }

    suspend fun planSafeRoute(
        origin: String,
        destination: String,
        notes: String = "",
        mode: RoutingEngineMode = RoutingEngineMode.AUTO,
        algorithmPrompt: String = ""
    ): Result<SafeRoutePlan> {
        // 1. Fetch the active engine name from routing_control
        val activeEngine = com.example.data.firebase.FirestoreService.fetchActiveEngineName()

        // 2. Fetch the corresponding configuration document
        val cloudConfig = try {
            com.example.data.firebase.FirestoreService.fetchRoutingConfig(activeEngine.lowercase())
        } catch (e: Exception) {
            null
        }

        if (cloudConfig != null && cloudConfig.apiKey.isNotBlank()) {
            val customPrompt = if (cloudConfig.prompt.isNotBlank()) cloudConfig.prompt else algorithmPrompt
            val apiType = cloudConfig.apiType.uppercase().trim()

            return when (apiType) {
                "TAVILY" -> {
                    val customModel = if (cloudConfig.modelName.isNotBlank()) cloudConfig.modelName else "gemini-3.5-flash"
                    tavilyService.generateSafeRoute(
                        origin = origin,
                        destination = destination,
                        routeNotes = notes,
                        algorithmPrompt = customPrompt,
                        tavilyApiKey = cloudConfig.apiKey,
                        customGeminiApiKey = cloudConfig.geminiApiKey,
                        modelName = customModel
                    )
                }
                "SILICONFLOW" -> {
                    val customModel = if (cloudConfig.modelName.isNotBlank()) cloudConfig.modelName else "deepseek-ai/DeepSeek-V3"
                    siliconFlowService.generateSafeRoute(origin, destination, notes, customPrompt, cloudConfig.apiKey, customModel)
                }
                "CLAUDE" -> {
                    val customModel = if (cloudConfig.modelName.isNotBlank()) cloudConfig.modelName else "claude-3-5-haiku-20241022"
                    claudeService.generateSafeRoute(origin, destination, notes, customPrompt, cloudConfig.apiKey, customModel)
                }
                "OPENROUTER" -> {
                    val customModel = if (cloudConfig.modelName.isNotBlank()) cloudConfig.modelName else "openai/gpt-4o"
                    openRouterService.generateSafeRoute(origin, destination, notes, customPrompt, cloudConfig.apiKey, customModel)
                }
                else -> { // Default to "GEMINI"
                    val customModel = if (cloudConfig.modelName.isNotBlank()) cloudConfig.modelName else "gemini-3.5-flash"
                    geminiService.generateSafeRoute(origin, destination, notes, customPrompt, cloudConfig.apiKey, customModel)
                }
            }
        }

        // 2. Local fallback modes if no cloud configuration is active or connected
        return when (mode) {
            RoutingEngineMode.DETERMINISTIC -> {
                Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }
            RoutingEngineMode.CLAUDE -> {
                claudeService.generateSafeRoute(origin, destination, notes, algorithmPrompt)
            }
            RoutingEngineMode.GEMINI -> {
                geminiService.generateSafeRoute(origin, destination, notes, algorithmPrompt)
            }
            RoutingEngineMode.SILICONFLOW -> {
                Result.success(HighwayCorridorEngine.resolveSafeRoute(origin, destination))
            }
            RoutingEngineMode.AUTO -> {
                geminiService.generateSafeRoute(origin, destination, notes, algorithmPrompt)
            }
        }
    }

    private fun RouteEntity.toSafeRoutePlan(): SafeRoutePlan {
        return SafeRoutePlan(
            id = id.toString(),
            title = title,
            origin = origin,
            destination = destination,
            distanceApprox = distanceApprox,
            primaryRoads = primaryRoads.split(",").map { it.trim() },
            mapsUrl = mapsUrl,
            stops = emptyList(),
            verifyLine = verifyLine,
            tips = RouteTips(
                bestTime = bestTime,
                avoid = avoid,
                localNote = localNote,
                corridorNote = corridorNote
            ),
            parking = RouteParking(
                bestLotName = bestLotName,
                bestLotType = bestLotType,
                bestLotMapLink = bestLotMapLink,
                backupLotName = backupLotName,
                backupLotType = backupLotType,
                backupLotMapLink = backupLotMapLink
            ),
            isCurated = false,
            userEmail = userEmail,
            carModel = carModel
        )
    }

    private fun SafeRoutePlan.toEntity(): RouteEntity {
        return RouteEntity(
            id = id,
            title = title,
            origin = origin,
            destination = destination,
            distanceApprox = distanceApprox,
            mapsUrl = mapsUrl,
            primaryRoads = primaryRoads.joinToString(", "),
            verifyLine = verifyLine,
            bestTime = tips.bestTime,
            avoid = tips.avoid,
            localNote = tips.localNote,
            corridorNote = tips.corridorNote,
            bestLotName = parking.bestLotName,
            bestLotType = parking.bestLotType,
            bestLotMapLink = parking.bestLotMapLink,
            backupLotName = parking.backupLotName,
            backupLotType = parking.backupLotType,
            backupLotMapLink = parking.backupLotMapLink,
            formattedOutput = toFormattedPromptOutput(),
            isFavorite = false,
            userEmail = userEmail,
            carModel = carModel
        )
    }

    fun getVehiclesForUser(email: String): Flow<List<com.example.data.local.VehicleEntity>> {
        return vehicleDao?.getVehiclesForUser(email) ?: kotlinx.coroutines.flow.flowOf(emptyList())
    }

    fun getActiveVehicle(email: String): Flow<com.example.data.local.VehicleEntity?> {
        return vehicleDao?.getDefaultVehicle(email) ?: kotlinx.coroutines.flow.flowOf(null)
    }

    suspend fun saveVehicle(car: CarDetails, email: String, isDefault: Boolean = false) {
        val entity = com.example.data.local.VehicleEntity.fromCarDetails(car, email, isDefault)
        vehicleDao?.insertVehicle(entity)
        if (isDefault) {
            vehicleDao?.selectActiveVehicle(entity.registrationNumber, email)
        }
        try {
            com.example.data.firebase.FirestoreService.saveVehicleToCloud(email, entity)
            if (isDefault) {
                val allVehicles = vehicleDao?.getVehiclesForUserOnce(email) ?: emptyList()
                allVehicles.forEach { veh ->
                    com.example.data.firebase.FirestoreService.saveVehicleToCloud(email, veh)
                }
            }
        } catch (_: Exception) {}
    }

    suspend fun deleteVehicle(vehicle: com.example.data.local.VehicleEntity) {
        vehicleDao?.deleteVehicle(vehicle)
        try {
            com.example.data.firebase.FirestoreService.deleteVehicleFromCloud(vehicle.userEmail, vehicle.registrationNumber)
        } catch (_: Exception) {}
    }

    suspend fun selectActiveVehicle(reg: String, email: String) {
        vehicleDao?.selectActiveVehicle(reg, email)
        try {
            val allVehicles = vehicleDao?.getVehiclesForUserOnce(email) ?: emptyList()
            allVehicles.forEach { veh ->
                com.example.data.firebase.FirestoreService.saveVehicleToCloud(email, veh)
            }
        } catch (_: Exception) {}
    }

    suspend fun syncVehiclesWithCloud(userEmail: String) {
        if (userEmail.isBlank()) return
        try {
            val cloudVehicles = com.example.data.firebase.FirestoreService.fetchCloudVehicles(userEmail)
            cloudVehicles.forEach { cloudVehicle ->
                vehicleDao?.insertVehicle(cloudVehicle)
            }
        } catch (_: Exception) {}
    }
}
