package com.example.data.model

data class GoogleUserProfile(
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val isLoggedIn: Boolean = false,
    val hasCompletedVehicleSetup: Boolean = false
)

data class CarDetails(
    val makeModel: String = "Tata Nexon",
    val variant: String = "Fearless+ 1.2 Turbo",
    val registrationNumber: String = "KA 01 AB 1234",
    val tyreSpecification: String = "215/60 R16 Alloys",
    val recommendedPsi: String = "32-34 PSI Cold",
    val hasConnectedCarBluelink: Boolean = false,
    val fastagProvider: String = "FASTag Active",
    val fuelType: String = "Petrol / Diesel",
    val oemRsaPhone: String = "1800 209 8282",
    val sosButtonInstruction: String = "Roof console SOS assist switch • Tata 24x7 RSA: 1800 209 8282 • NHAI: 1033"
)

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
