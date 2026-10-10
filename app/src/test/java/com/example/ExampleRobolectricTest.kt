package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.ClaudeRouteService
import com.example.data.curated.CuratedRoutes
import com.example.data.local.UserSessionManager
import com.example.data.model.AppThemeMode
import com.example.data.model.CarDetails
import com.example.data.repository.RoutingEngineMode
import com.example.data.routing.HighwayCorridorEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SafeTrip", appName)
    }

    @Test
    fun `verify curated routes have valid maps dir url and emergency block`() {
        val routes = CuratedRoutes.routes
        assertTrue(routes.isNotEmpty())

        val chennaiBengaluru = routes.find { it.id == "chennai-bengaluru" }
        assertNotNull(chennaiBengaluru)

        assertTrue(chennaiBengaluru!!.mapsUrl.startsWith("https://www.google.com/maps/dir/"))
        assertTrue(chennaiBengaluru.mapsUrl.endsWith("/"))
        assertEquals("1800 102 4645", chennaiBengaluru.emergency.hyundaiRsaPhone)
        assertEquals("1033", chennaiBengaluru.emergency.nhaiHighwayEmergency)

        val formattedText = chennaiBengaluru.toFormattedPromptOutput()
        assertTrue(formattedText.contains("🗺️ Route"))
        assertTrue(formattedText.contains("📝 Tips"))
        assertTrue(formattedText.contains("🅿️ Parking"))
        assertTrue(formattedText.contains("🆘 Emergency"))
        assertTrue(formattedText.contains("1800 102 4645"))
        assertTrue(formattedText.contains("1033"))
    }

    @Test
    fun `verify deterministic highway corridor engine without AI`() {
        val plan = HighwayCorridorEngine.resolveSafeRoute("Adyar, Chennai", "Indiranagar, Bengaluru")
        assertNotNull(plan)
        assertTrue(plan.mapsUrl.startsWith("https://www.google.com/maps/dir/"))
        assertTrue(plan.mapsUrl.contains("Vandalur+Outer+Ring+Road+Toll+Plaza+Chennai+ORR"))
        assertTrue(plan.stops.isNotEmpty())
        assertTrue(plan.verifyLine.contains("✅ Road numbers used"))
        assertNotNull(plan.parking.bestLotMapLink)
    }

    @Test
    fun `verify claude service falls back gracefully when key is placeholder`() = runBlocking {
        val claudeService = ClaudeRouteService()
        val result = claudeService.generateSafeRoute("Bandra West, Mumbai", "Panaji, Goa")
        assertTrue(result.isSuccess)
        val plan = result.getOrNull()
        assertNotNull(plan)
        assertTrue(plan!!.mapsUrl.contains("Khalapur+Toll+Plaza+Mumbai+Pune+Expressway"))
    }

    @Test
    fun `verify routing engine modes`() {
        assertEquals(4, RoutingEngineMode.values().size)
        assertTrue(RoutingEngineMode.values().contains(RoutingEngineMode.CLAUDE))
        assertTrue(RoutingEngineMode.values().contains(RoutingEngineMode.GEMINI))
        assertTrue(RoutingEngineMode.values().contains(RoutingEngineMode.DETERMINISTIC))
    }

    @Test
    fun `verify user session and car details update`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sessionManager = UserSessionManager(context)

        val newCar = CarDetails(
            makeModel = "Hyundai Creta N Line",
            variant = "N8 1.5 Turbo DCT",
            registrationNumber = "KA 01 MG 9999",
            tyreSpecification = "215/55 R18 Alloys",
            recommendedPsi = "35 PSI Cold",
            hasConnectedCarBluelink = true
        )

        sessionManager.signInWithGoogle("mkkarthiik@gmail.com", "Karthiik", newCar)

        val user = sessionManager.userProfile.value
        assertEquals("mkkarthiik@gmail.com", user.email)
        assertTrue(user.isLoggedIn)

        val car = sessionManager.carDetails.value
        assertEquals("Hyundai Creta N Line", car.makeModel)
        assertEquals("KA 01 MG 9999", car.registrationNumber)

        sessionManager.setThemeMode(AppThemeMode.LIGHT)
        assertEquals(AppThemeMode.LIGHT, sessionManager.themeMode.value)

        sessionManager.signOut()
        assertEquals(false, sessionManager.userProfile.value.isLoggedIn)

        sessionManager.updateApiKeys("test-gemini-key", "test-claude-key", "test-google-key")
        assertEquals("test-gemini-key", sessionManager.geminiApiKey.value)
        assertEquals("test-claude-key", sessionManager.claudeApiKey.value)
        assertEquals("test-google-key", sessionManager.googlePlacesApiKey.value)
    }

    @Test
    fun `verify place autocomplete service returns highway hubs`() = runBlocking {
        val results = com.example.data.api.PlaceAutocompleteService.searchPlaces("Chennai")
        assertTrue(results.isNotEmpty())
        val match = results.find { it.title.contains("Chennai") || it.subtitle.contains("Chennai") }
        assertNotNull(match)
    }

    @Test
    fun `verify extended vehicle database has searchable models`() {
        val database = com.example.data.vehicles.IndianVehicleDatabase.vehicles
        assertTrue(database.size >= 15)

        val creta = database.find { it.makeModel.contains("Creta") }
        assertNotNull(creta)
        assertEquals("215/60 R17 Diamond Cut", creta!!.tyreSpec)
        assertEquals("33-35 PSI Cold", creta.recommendedPsi)

        val virtus = database.find { it.makeModel.contains("Virtus") }
        assertNotNull(virtus)
        assertEquals("205/55 R16 Razor Alloys", virtus!!.tyreSpec)
    }
}
