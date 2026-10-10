package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.CarDetails
import com.example.data.model.GoogleUserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("safe_route_user_session", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<GoogleUserProfile> = _userProfile.asStateFlow()

    private val _carDetails = MutableStateFlow(loadCarDetails())
    val carDetails: StateFlow<CarDetails> = _carDetails.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString(KEY_GEMINI_KEY, "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _claudeApiKey = MutableStateFlow(prefs.getString(KEY_CLAUDE_KEY, "") ?: "")
    val claudeApiKey: StateFlow<String> = _claudeApiKey.asStateFlow()

    private val _googlePlacesApiKey = MutableStateFlow(prefs.getString(KEY_GOOGLE_PLACES_KEY, "") ?: "")
    val googlePlacesApiKey: StateFlow<String> = _googlePlacesApiKey.asStateFlow()

    private fun loadUserProfile(): GoogleUserProfile {
        val firebaseUser = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }

        if (firebaseUser != null) {
            val email = firebaseUser.email.orEmpty()
            val displayName = firebaseUser.displayName.orEmpty().ifBlank { "Google Driver" }
            val hasSetup = prefs.getBoolean(KEY_HAS_COMPLETED_SETUP, false)
            return GoogleUserProfile(
                email = email,
                displayName = displayName,
                photoUrl = firebaseUser.photoUrl?.toString().orEmpty(),
                isLoggedIn = true,
                hasCompletedVehicleSetup = hasSetup
            )
        }

        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val name = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
        val hasSetup = prefs.getBoolean(KEY_HAS_COMPLETED_SETUP, false)
        return GoogleUserProfile(
            email = email,
            displayName = name,
            photoUrl = "",
            isLoggedIn = isLoggedIn && email.isNotBlank(),
            hasCompletedVehicleSetup = hasSetup
        )
    }

    private fun loadCarDetails(): CarDetails {
        return CarDetails(
            makeModel = prefs.getString(KEY_CAR_MAKE, "Tata Nexon") ?: "Tata Nexon",
            variant = prefs.getString(KEY_CAR_VARIANT, "Fearless+ 1.2 Turbo") ?: "Fearless+ 1.2 Turbo",
            registrationNumber = prefs.getString(KEY_CAR_REG, "KA 01 AB 1234") ?: "KA 01 AB 1234",
            tyreSpecification = prefs.getString(KEY_CAR_TYRE, "215/60 R16 Alloys") ?: "215/60 R16 Alloys",
            recommendedPsi = prefs.getString(KEY_CAR_PSI, "32-34 PSI Cold") ?: "32-34 PSI Cold",
            hasConnectedCarBluelink = prefs.getBoolean(KEY_CAR_BLUELINK, false),
            fastagProvider = prefs.getString(KEY_CAR_FASTAG, "FASTag Active") ?: "FASTag Active",
            fuelType = prefs.getString(KEY_CAR_FUEL, "Petrol / Diesel") ?: "Petrol / Diesel",
            oemRsaPhone = prefs.getString(KEY_CAR_RSA, "1800 209 8282") ?: "1800 209 8282",
            sosButtonInstruction = prefs.getString(KEY_CAR_SOS, "Roof console SOS assist switch • Tata 24x7 RSA: 1800 209 8282 • NHAI: 1033") ?: "Roof console SOS assist switch • Tata 24x7 RSA: 1800 209 8282 • NHAI: 1033"
        )
    }

    private fun loadThemeMode(): AppThemeMode {
        val modeStr = prefs.getString(KEY_THEME_MODE, AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
        return try {
            AppThemeMode.valueOf(modeStr)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }
    }

    fun signInWithGoogle(email: String, displayName: String, carDetails: CarDetails? = null, markSetupComplete: Boolean = true) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_EMAIL, email)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putBoolean(KEY_HAS_COMPLETED_SETUP, markSetupComplete)
            .apply()

        _userProfile.value = GoogleUserProfile(
            email = email,
            displayName = displayName,
            photoUrl = "",
            isLoggedIn = true,
            hasCompletedVehicleSetup = markSetupComplete
        )

        if (carDetails != null) {
            updateCarDetails(carDetails)
        }
    }

    fun completeVehicleSetup(details: CarDetails) {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_SETUP, true)
            .apply()
        updateCarDetails(details)
        _userProfile.value = _userProfile.value.copy(hasCompletedVehicleSetup = true)
    }

    fun markVehicleSetupCompleteOnly() {
        prefs.edit()
            .putBoolean(KEY_HAS_COMPLETED_SETUP, true)
            .apply()
        _userProfile.value = _userProfile.value.copy(hasCompletedVehicleSetup = true)
    }

    fun signOut() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_EMAIL, "")
            .putString(KEY_DISPLAY_NAME, "")
            .putBoolean(KEY_HAS_COMPLETED_SETUP, false)
            .apply()
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // ignore
        }
        _userProfile.value = GoogleUserProfile(
            email = "",
            displayName = "",
            photoUrl = "",
            isLoggedIn = false,
            hasCompletedVehicleSetup = false
        )
    }

    fun updateCarDetails(details: CarDetails) {
        prefs.edit()
            .putString(KEY_CAR_MAKE, details.makeModel)
            .putString(KEY_CAR_VARIANT, details.variant)
            .putString(KEY_CAR_REG, details.registrationNumber)
            .putString(KEY_CAR_TYRE, details.tyreSpecification)
            .putString(KEY_CAR_PSI, details.recommendedPsi)
            .putBoolean(KEY_CAR_BLUELINK, details.hasConnectedCarBluelink)
            .putString(KEY_CAR_FASTAG, details.fastagProvider)
            .putString(KEY_CAR_FUEL, details.fuelType)
            .putString(KEY_CAR_RSA, details.oemRsaPhone)
            .putString(KEY_CAR_SOS, details.sosButtonInstruction)
            .apply()

        _carDetails.value = details
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    fun updateApiKeys(gemini: String, claude: String, googlePlaces: String) {
        prefs.edit()
            .putString(KEY_GEMINI_KEY, gemini.trim())
            .putString(KEY_CLAUDE_KEY, claude.trim())
            .putString(KEY_GOOGLE_PLACES_KEY, googlePlaces.trim())
            .apply()
        _geminiApiKey.value = gemini.trim()
        _claudeApiKey.value = claude.trim()
        _googlePlacesApiKey.value = googlePlaces.trim()
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_DISPLAY_NAME = "user_display_name"

        private const val KEY_CAR_MAKE = "car_make"
        private const val KEY_CAR_VARIANT = "car_variant"
        private const val KEY_CAR_REG = "car_reg"
        private const val KEY_CAR_TYRE = "car_tyre"
        private const val KEY_CAR_PSI = "car_psi"
        private const val KEY_CAR_BLUELINK = "car_bluelink"
        private const val KEY_CAR_FASTAG = "car_fastag"
        private const val KEY_CAR_FUEL = "car_fuel"
        private const val KEY_CAR_RSA = "car_rsa"
        private const val KEY_CAR_SOS = "car_sos"
        private const val KEY_HAS_COMPLETED_SETUP = "has_completed_setup"

        private const val KEY_THEME_MODE = "theme_mode"

        private const val KEY_GEMINI_KEY = "custom_gemini_api_key"
        private const val KEY_CLAUDE_KEY = "custom_claude_api_key"
        private const val KEY_GOOGLE_PLACES_KEY = "custom_google_places_api_key"
    }
}
