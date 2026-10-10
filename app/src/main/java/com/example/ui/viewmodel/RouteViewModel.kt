package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.UserSessionManager
import com.example.data.model.AppThemeMode
import com.example.data.model.CarDetails
import com.example.data.model.GoogleUserProfile
import com.example.data.model.SafeRoutePlan
import com.example.data.repository.RouteRepository
import com.example.data.repository.RoutingEngineMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface PlanUiState {
    data object Idle : PlanUiState
    data object Loading : PlanUiState
    data class Success(val plan: SafeRoutePlan) : PlanUiState
    data class Error(val message: String) : PlanUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RouteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RouteRepository
    private val sessionManager = UserSessionManager(application)

    val userProfile: StateFlow<GoogleUserProfile> = sessionManager.userProfile
    val carDetails: StateFlow<CarDetails> = sessionManager.carDetails
    val themeMode: StateFlow<AppThemeMode> = sessionManager.themeMode

    val geminiApiKey: StateFlow<String> = sessionManager.geminiApiKey
    val claudeApiKey: StateFlow<String> = sessionManager.claudeApiKey
    val googlePlacesApiKey: StateFlow<String> = sessionManager.googlePlacesApiKey

    init {
        // Initialize Firestore with custom database ID first
        com.example.data.firebase.FirestoreService.init(application)

        val db = AppDatabase.getDatabase(application)
        repository = RouteRepository(db.routeDao(), db.vehicleDao())

        // Seed / initialize the Tavily / Gemini config on startup if they have given us Firebase access
        viewModelScope.launch {
            try {
                com.example.data.firebase.FirestoreService.initializeTavilyConfigInCloud()
            } catch (_: Exception) {}
        }

        // Auto-populate the database with the default car if empty on start
        viewModelScope.launch {
            userProfile.collect { profile ->
                if (profile.email.isNotBlank()) {
                    try {
                        repository.syncVehiclesWithCloud(profile.email)
                    } catch (_: Exception) {}
                }
            }
        }

        viewModelScope.launch {
            userProfile
                .flatMapLatest { profile ->
                    if (profile.email.isNotBlank()) {
                        repository.getVehiclesForUser(profile.email)
                    } else {
                        kotlinx.coroutines.flow.flowOf(emptyList())
                    }
                }
                .collect { vehiclesList ->
                    val email = userProfile.value.email
                    if (email.isNotBlank() && vehiclesList.isEmpty()) {
                        try {
                            repository.saveVehicle(carDetails.value, email, isDefault = true)
                        } catch (_: Exception) {}
                    }
                }
        }

        // Collect active vehicle from database to keep SharedPreferences/Cached Flow automatically in sync
        viewModelScope.launch {
            userProfile
                .flatMapLatest { profile ->
                    if (profile.email.isNotBlank()) {
                        repository.getActiveVehicle(profile.email)
                    } else {
                        kotlinx.coroutines.flow.flowOf(null)
                    }
                }
                .collect { activeVehicle ->
                    if (activeVehicle != null) {
                        sessionManager.updateCarDetails(activeVehicle.toCarDetails())
                        if (!userProfile.value.hasCompletedVehicleSetup) {
                            sessionManager.markVehicleSetupCompleteOnly()
                        }
                    }
                }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val vehicles: StateFlow<List<com.example.data.local.VehicleEntity>> = userProfile
        .flatMapLatest { profile ->
            repository.getVehiclesForUser(profile.email)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateApiKeys(gemini: String, claude: String, googlePlaces: String) {
        sessionManager.updateApiKeys(gemini, claude, googlePlaces)
        Toast.makeText(getApplication(), "API keys updated successfully!", Toast.LENGTH_SHORT).show()
    }

    val curatedRoutes: List<SafeRoutePlan> = repository.getCuratedRoutes()

    val savedRoutes: StateFlow<List<SafeRoutePlan>> = repository.savedRoutes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentRoute = MutableStateFlow<SafeRoutePlan?>(curatedRoutes.firstOrNull())
    val currentRoute: StateFlow<SafeRoutePlan?> = _currentRoute.asStateFlow()

    private val _planUiState = MutableStateFlow<PlanUiState>(PlanUiState.Idle)
    val planUiState: StateFlow<PlanUiState> = _planUiState.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _engineMode = MutableStateFlow(RoutingEngineMode.AUTO)
    val engineMode: StateFlow<RoutingEngineMode> = _engineMode.asStateFlow()

    var lastNavigatedPlanId: String? = null

    fun resetPlanState() {
        _planUiState.value = PlanUiState.Idle
        lastNavigatedPlanId = null
    }

    fun setEngineMode(mode: RoutingEngineMode) {
        _engineMode.value = mode
    }

    fun selectRoute(plan: SafeRoutePlan) {
        _currentRoute.value = plan
    }

    fun selectRouteById(id: String) {
        val plan = curatedRoutes.find { it.id == id }
        if (plan != null) {
            _currentRoute.value = plan
        }
    }

    fun generateRouteFromPastedText(clipboardText: String, origin: String, destination: String) {
        lastNavigatedPlanId = null
        if (clipboardText.isBlank()) {
            _planUiState.value = PlanUiState.Error("Clipboard is empty. Please copy some route format text first.")
            return
        }

        viewModelScope.launch {
            _planUiState.value = PlanUiState.Loading
            try {
                // Instantly parse the clipboard route format using the Gemini parser directly
                val geminiService = com.example.data.api.GeminiRouteService()
                val parsedPlan = geminiService.parseFormattedText(clipboardText, origin, destination)

                val user = userProfile.value
                val car = carDetails.value
                val planWithUser = parsedPlan.copy(
                    id = "custom_${System.currentTimeMillis()}",
                    userEmail = user.email,
                    carModel = car.makeModel
                )
                kotlinx.coroutines.delay(3000)
                _planUiState.value = PlanUiState.Success(planWithUser)
                _currentRoute.value = planWithUser
            } catch (e: Exception) {
                _planUiState.value = PlanUiState.Error("Failed to parse the pasted route format. Verify that you have copied the full route output text.")
            }
        }
    }

    fun planCustomRoute(
        origin: String,
        destination: String,
        notes: String = "",
        mode: RoutingEngineMode = _engineMode.value
    ) {
        lastNavigatedPlanId = null
        if (origin.isBlank() || destination.isBlank()) {
            _planUiState.value = PlanUiState.Error("Please enter both origin and destination cities/places.")
            return
        }

        val car = carDetails.value
        val enhancedNotes = buildString {
            if (notes.isNotBlank()) append(notes).append(". ")
            append("Vehicle: ").append(car.makeModel).append(" (").append(car.variant).append("), ")
            append("Tyres: ").append(car.tyreSpecification).append(" (").append(car.recommendedPsi).append("). ")
            append("Connected Car: ").append(if (car.hasConnectedCarBluelink) "Active Bluelink with RSA/SOS buttons" else "Standard").append(". ")
            append("FASTag: ").append(car.fastagProvider)
        }

        viewModelScope.launch {
            _planUiState.value = PlanUiState.Loading
            var algorithmPrompt = com.example.data.firebase.FirestoreService.fetchRoutingPrompt()
            if (algorithmPrompt.isNullOrBlank()) {
                algorithmPrompt = try {
                    getApplication<android.app.Application>().assets.open("route.md").bufferedReader().use { it.readText() }
                } catch (e: Exception) {
                    ""
                }
            }
            val result = repository.planSafeRoute(origin, destination, enhancedNotes, mode, algorithmPrompt)
            result.fold(
                onSuccess = { plan ->
                    val user = userProfile.value
                    val planWithUser = plan.copy(
                        userEmail = user.email,
                        carModel = car.makeModel
                    )
                    _planUiState.value = PlanUiState.Success(planWithUser)
                    _currentRoute.value = planWithUser
                },
                onFailure = { err ->
                    _planUiState.value = PlanUiState.Error(err.message ?: "Failed to generate safe route.")
                }
            )
        }
    }

    fun saveCurrentRoute() {
        val route = _currentRoute.value ?: return
        val user = userProfile.value
        val car = carDetails.value
        viewModelScope.launch {
            repository.saveRoute(route, userEmail = user.email, carModel = car.makeModel)
            Toast.makeText(getApplication(), "Route saved to Room & Cloud Firestore (${user.email})!", Toast.LENGTH_SHORT).show()
        }
    }

    fun syncWithCloud() {
        val user = userProfile.value
        if (user.email.isBlank()) return
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                repository.syncRoutesWithCloud(user.email)
                repository.syncVehiclesWithCloud(user.email)
                Toast.makeText(getApplication(), "Trips & vehicles synced with cloud!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(getApplication(), "Sync failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun saveVerifiedParkingPin(name: String, city: String, mapLink: String, type: String = "Secure Covered Parking") {
        val user = userProfile.value
        viewModelScope.launch {
            val pin = com.example.data.firebase.CloudParkingPin(
                name = name,
                city = city,
                locationType = type,
                mapLink = mapLink,
                verifiedBy = user.email
            )
            val success = com.example.data.firebase.FirestoreService.saveParkingPin(pin)
            if (success) {
                Toast.makeText(getApplication(), "Parking pin synced to Cloud Firestore!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), "Saved locally (Cloud offline)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSavedRoute(id: String) {
        viewModelScope.launch {
            repository.deleteRoute(id)
            Toast.makeText(getApplication(), "Route removed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun signInWithGoogle(email: String, name: String, car: CarDetails? = null, markSetupComplete: Boolean = false) {
        sessionManager.signInWithGoogle(email, name, car, markSetupComplete = markSetupComplete)
        viewModelScope.launch {
            repository.syncRoutesWithCloud(email)
            repository.syncVehiclesWithCloud(email)
        }
        Toast.makeText(getApplication(), "Signed in as $email.", Toast.LENGTH_SHORT).show()
    }

    fun completeVehicleSetup(car: CarDetails) {
        sessionManager.completeVehicleSetup(car)
        viewModelScope.launch {
            repository.saveVehicle(car, userProfile.value.email, isDefault = true)
        }
        Toast.makeText(getApplication(), "Configured for ${car.makeModel} with SOS protocols!", Toast.LENGTH_SHORT).show()
    }

    fun signOut() {
        sessionManager.signOut()
        Toast.makeText(getApplication(), "Signed out of Google Account.", Toast.LENGTH_SHORT).show()
    }

    fun updateCarDetails(car: CarDetails) {
        sessionManager.updateCarDetails(car)
        viewModelScope.launch {
            repository.saveVehicle(car, userProfile.value.email, isDefault = true)
        }
        Toast.makeText(getApplication(), "Car profile updated to ${car.makeModel}!", Toast.LENGTH_SHORT).show()
    }

    fun saveVehicle(car: CarDetails, isDefault: Boolean = false) {
        viewModelScope.launch {
            repository.saveVehicle(car, userProfile.value.email, isDefault)
            if (isDefault) {
                sessionManager.updateCarDetails(car)
            }
        }
    }

    fun selectActiveVehicle(reg: String) {
        viewModelScope.launch {
            repository.selectActiveVehicle(reg, userProfile.value.email)
        }
    }

    fun deleteVehicle(vehicle: com.example.data.local.VehicleEntity) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        sessionManager.setThemeMode(mode)
    }

    fun saveRoutingConfigToCloud(apiKey: String, apiType: String = "GEMINI", modelName: String = "gemini-3.5-flash", prompt: String = "") {
        viewModelScope.launch {
            val config = com.example.data.firebase.CloudRoutingConfig(
                apiKey = apiKey,
                apiType = apiType,
                modelName = modelName,
                prompt = prompt
            )
            val success = com.example.data.firebase.FirestoreService.saveRoutingConfig(config)
            if (success) {
                if (apiType == "GEMINI") {
                    sessionManager.updateApiKeys(gemini = apiKey, claude = sessionManager.claudeApiKey.value, googlePlaces = sessionManager.googlePlacesApiKey.value)
                } else if (apiType == "CLAUDE") {
                    sessionManager.updateApiKeys(gemini = sessionManager.geminiApiKey.value, claude = apiKey, googlePlaces = sessionManager.googlePlacesApiKey.value)
                }
                Toast.makeText(getApplication(), "API Key saved to Firebase Firestore & synced!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(getApplication(), "Failed to save API key to cloud. Ensure Firestore is configured.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toggleThemeMode() {
        val current = themeMode.value
        val next = if (current == AppThemeMode.LIGHT) AppThemeMode.DARK else AppThemeMode.LIGHT
        setThemeMode(next)
        val modeName = if (next == AppThemeMode.LIGHT) "Light Mode" else "Dark Mode"
        Toast.makeText(getApplication(), "Switched to $modeName", Toast.LENGTH_SHORT).show()
    }

    fun openMaps(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open Maps: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    fun copyPromptOutput(context: Context, plan: SafeRoutePlan) {
        val text = plan.toFormattedPromptOutput()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SafeRoute Plan", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Safe Route text copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareRoute(context: Context, plan: SafeRoutePlan) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, plan.toFormattedPromptOutput())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Safe Route via")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }
}
