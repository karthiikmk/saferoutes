package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.AppThemeMode
import com.example.data.model.SafeRoutePlan
import com.example.ui.screens.CarDetailsScreen
import com.example.ui.screens.EmergencyHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RouteDetailScreen
import com.example.ui.screens.RoutePlannerScreen
import com.example.ui.screens.SavedRoutesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VehicleSetupScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.RouteViewModel

enum class AppNavScreen {
    HOME,
    PLANNER,
    SAVED,
    EMERGENCY,
    SETTINGS,
    DETAIL,
    CAR_DETAILS,
    VEHICLE_SETUP
}

class MainActivity : ComponentActivity() {

    private val viewModel: RouteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = themeMode) {
                MainAppNavHost(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppNavHost(viewModel: RouteViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val navStack = remember { mutableStateListOf(AppNavScreen.HOME) }
    val currentScreen = navStack.lastOrNull() ?: AppNavScreen.HOME
    var selectedRoutePlan by remember { mutableStateOf<SafeRoutePlan?>(null) }

    fun navigateTo(screen: AppNavScreen) {
        if (navStack.lastOrNull() != screen) {
            navStack.add(screen)
        }
    }

    fun navigateBack() {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.lastIndex)
        } else {
            navStack.clear()
            navStack.add(AppNavScreen.HOME)
        }
    }

    fun switchTab(screen: AppNavScreen) {
        navStack.clear()
        navStack.add(screen)
    }

    // If logged out: take user completely out of the tabs and show the full-screen Google Login page
    if (!userProfile.isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = {
                // Handled automatically by checking hasCompletedVehicleSetup
            }
        )
        return
    }

    // Intermediate page after login: search & choose vehicle with auto-constructed SOS
    if (!userProfile.hasCompletedVehicleSetup) {
        VehicleSetupScreen(
            viewModel = viewModel,
            onSetupComplete = {
                switchTab(AppNavScreen.HOME)
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom navigation on the 5 primary tabs
            val isPrimaryTab = currentScreen in listOf(
                AppNavScreen.HOME,
                AppNavScreen.PLANNER,
                AppNavScreen.SAVED,
                AppNavScreen.EMERGENCY,
                AppNavScreen.SETTINGS
            )
            if (isPrimaryTab) {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    // 1st Tab: Home / Corridors
                    NavigationBarItem(
                        selected = currentScreen == AppNavScreen.HOME,
                        onClick = { switchTab(AppNavScreen.HOME) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    // 2nd Tab: Plan Trip
                    NavigationBarItem(
                        selected = currentScreen == AppNavScreen.PLANNER,
                        onClick = { switchTab(AppNavScreen.PLANNER) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Plan"
                            )
                        },
                        label = { Text("Plan") },
                        modifier = Modifier.testTag("nav_item_planner")
                    )

                    // 3rd Tab: Saved ("Saved can be the 3rd tab")
                    NavigationBarItem(
                        selected = currentScreen == AppNavScreen.SAVED,
                        onClick = { switchTab(AppNavScreen.SAVED) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Saved"
                            )
                        },
                        label = { Text("Saved") },
                        modifier = Modifier.testTag("nav_item_saved")
                    )

                    // 4th Tab: SOS / Emergency ("SOS can be 4th")
                    NavigationBarItem(
                        selected = currentScreen == AppNavScreen.EMERGENCY,
                        onClick = { switchTab(AppNavScreen.EMERGENCY) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "SOS"
                            )
                        },
                        label = { Text("SOS") },
                        modifier = Modifier.testTag("nav_item_emergency")
                    )

                    // 5th Tab: Settings ("Settings should be 5th")
                    NavigationBarItem(
                        selected = currentScreen == AppNavScreen.SETTINGS,
                        onClick = { switchTab(AppNavScreen.SETTINGS) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        },
                        label = { Text("Settings") },
                        modifier = Modifier.testTag("nav_item_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppNavScreen.HOME -> {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToPlanner = { switchTab(AppNavScreen.PLANNER) },
                    onNavigateToDetail = { plan ->
                        selectedRoutePlan = plan
                        viewModel.selectRoute(plan)
                        navigateTo(AppNavScreen.DETAIL)
                    },
                    onNavigateToEmergency = { switchTab(AppNavScreen.EMERGENCY) },
                    onNavigateToCarDetails = { navigateTo(AppNavScreen.CAR_DETAILS) },
                    onNavigateToSaved = { switchTab(AppNavScreen.SAVED) },
                    onNavigateToVehicleSetup = { navigateTo(AppNavScreen.VEHICLE_SETUP) }
                )
            }

            AppNavScreen.PLANNER -> {
                BackHandler { switchTab(AppNavScreen.HOME) }
                RoutePlannerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { switchTab(AppNavScreen.HOME) },
                    onNavigateToDetail = { plan ->
                        selectedRoutePlan = plan
                        viewModel.selectRoute(plan)
                        navigateTo(AppNavScreen.DETAIL)
                    }
                )
            }

            AppNavScreen.SAVED -> {
                BackHandler { switchTab(AppNavScreen.HOME) }
                SavedRoutesScreen(
                    viewModel = viewModel,
                    onNavigateBack = { switchTab(AppNavScreen.HOME) },
                    onNavigateToDetail = { plan ->
                        selectedRoutePlan = plan
                        viewModel.selectRoute(plan)
                        navigateTo(AppNavScreen.DETAIL)
                    }
                )
            }

            AppNavScreen.EMERGENCY -> {
                BackHandler { switchTab(AppNavScreen.HOME) }
                EmergencyHubScreen(
                    onNavigateBack = { switchTab(AppNavScreen.HOME) },
                    viewModel = viewModel
                )
            }

            AppNavScreen.SETTINGS -> {
                BackHandler { switchTab(AppNavScreen.HOME) }
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { switchTab(AppNavScreen.HOME) },
                    onNavigateToCarDetails = { navigateTo(AppNavScreen.CAR_DETAILS) },
                    onLogoutSuccess = {
                        switchTab(AppNavScreen.HOME)
                    },
                    onNavigateToVehicleSetup = { navigateTo(AppNavScreen.VEHICLE_SETUP) }
                )
            }

            AppNavScreen.CAR_DETAILS -> {
                BackHandler { navigateBack() }
                CarDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navigateBack() },
                    onNavigateToVehicleSetup = { navigateTo(AppNavScreen.VEHICLE_SETUP) }
                )
            }

            AppNavScreen.VEHICLE_SETUP -> {
                BackHandler { navigateBack() }
                VehicleSetupScreen(
                    viewModel = viewModel,
                    onSetupComplete = { navigateBack() },
                    onNavigateBack = { navigateBack() }
                )
            }

            AppNavScreen.DETAIL -> {
                BackHandler { navigateBack() }
                val currentPlan = selectedRoutePlan ?: viewModel.currentRoute.collectAsState().value
                if (currentPlan != null) {
                    RouteDetailScreen(
                        plan = currentPlan,
                        viewModel = viewModel,
                        onNavigateBack = { navigateBack() }
                    )
                } else {
                    switchTab(AppNavScreen.HOME)
                }
            }
        }
    }
}
