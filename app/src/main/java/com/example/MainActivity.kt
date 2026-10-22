package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.model.PlantItem
import com.example.ui.auth.FirebaseInstructionsDialog
import com.example.ui.auth.ForgotPasswordScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.components.AddEditPlantDialog
import com.example.ui.components.WaterActionDialog
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlantDetailScreen
import com.example.ui.screens.PlantsScreen
import com.example.ui.screens.ScanScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PlantPalTheme
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.PlantPalViewModel

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    PLANTS("My Plants", Icons.Filled.Eco, Icons.Outlined.Eco),
    SCAN("Scan", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    DIARY("Diary", Icons.Filled.Book, Icons.Outlined.Book),
    ASSISTANT("Assistant", Icons.Filled.Psychology, Icons.Outlined.Psychology)
}

sealed class AppScreen {
    object Login : AppScreen()
    object Register : AppScreen()
    object ForgotPassword : AppScreen()
    object Onboarding : AppScreen()
    object MainNavigation : AppScreen()
    data class PlantDetail(val plant: PlantItem) : AppScreen()
    object Settings : AppScreen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: PlantPalViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlantPalTheme {
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Login) }
                var selectedTab by remember { mutableStateOf(MainTab.HOME) }
                var showFirebaseInstructionsDialog by remember { mutableStateOf(false) }

                var plantForWaterDialog by remember { mutableStateOf<PlantItem?>(null) }
                var showAddPlantDialog by remember { mutableStateOf(false) }

                when (val screen = currentScreen) {
                    is AppScreen.Login -> {
                        LoginScreen(
                            viewModel = authViewModel,
                            onLoginSuccess = { currentScreen = AppScreen.MainNavigation },
                            onNavigateToRegister = { currentScreen = AppScreen.Register },
                            onNavigateToForgotPassword = { currentScreen = AppScreen.ForgotPassword },
                            onShowFirebaseInstructions = { showFirebaseInstructionsDialog = true }
                        )
                    }

                    is AppScreen.Register -> {
                        RegisterScreen(
                            viewModel = authViewModel,
                            onRegisterSuccess = { currentScreen = AppScreen.Onboarding },
                            onNavigateToLogin = { currentScreen = AppScreen.Login },
                            onShowFirebaseInstructions = { showFirebaseInstructionsDialog = true }
                        )
                    }

                    is AppScreen.ForgotPassword -> {
                        ForgotPasswordScreen(
                            viewModel = authViewModel,
                            onBack = { currentScreen = AppScreen.Login }
                        )
                    }

                    is AppScreen.Onboarding -> {
                        OnboardingScreen(
                            onFinish = { currentScreen = AppScreen.MainNavigation }
                        )
                    }

                    is AppScreen.PlantDetail -> {
                        PlantDetailScreen(
                            plant = screen.plant,
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.MainNavigation }
                        )
                    }

                    is AppScreen.Settings -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = AppScreen.MainNavigation },
                            onReplayOnboarding = { currentScreen = AppScreen.Onboarding },
                            onLogout = {
                                authViewModel.logout()
                                currentScreen = AppScreen.Login
                            }
                        )
                    }

                    is AppScreen.MainNavigation -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                if (selectedTab != MainTab.HOME) {
                                    MainTopBar(
                                        title = when (selectedTab) {
                                            MainTab.PLANTS -> "Garden Library"
                                            MainTab.SCAN -> "AI Health Diagnostic"
                                            MainTab.DIARY -> "Plant Diary"
                                            MainTab.ASSISTANT -> "PlantPal AI"
                                            else -> "PlantPal"
                                        },
                                        onSettingsClick = { currentScreen = AppScreen.Settings }
                                    )
                                }
                            },
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier
                                        .navigationBarsPadding()
                                        .testTag("bottom_nav_bar")
                                ) {
                                    MainTab.values().forEach { tab ->
                                        val isSelected = selectedTab == tab
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = { selectedTab = tab },
                                            icon = {
                                                Icon(
                                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                    contentDescription = tab.title
                                                )
                                            },
                                            label = { Text(tab.title) },
                                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                when (selectedTab) {
                                    MainTab.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToPlants = { selectedTab = MainTab.PLANTS },
                                        onNavigateToScan = { selectedTab = MainTab.SCAN },
                                        onNavigateToDiary = { selectedTab = MainTab.DIARY },
                                        onNavigateToAssistant = { selectedTab = MainTab.ASSISTANT },
                                        onPlantClick = { plant ->
                                            viewModel.selectPlant(plant)
                                            currentScreen = AppScreen.PlantDetail(plant)
                                        },
                                        onAddPlantClick = { showAddPlantDialog = true },
                                        onWaterClick = { plant -> plantForWaterDialog = plant }
                                    )

                                    MainTab.PLANTS -> PlantsScreen(
                                        viewModel = viewModel,
                                        onPlantClick = { plant ->
                                            viewModel.selectPlant(plant)
                                            currentScreen = AppScreen.PlantDetail(plant)
                                        },
                                        onAddPlantClick = { showAddPlantDialog = true },
                                        onWaterClick = { plant -> plantForWaterDialog = plant }
                                    )

                                    MainTab.SCAN -> ScanScreen(
                                        viewModel = viewModel
                                    )

                                    MainTab.DIARY -> DiaryScreen(
                                        viewModel = viewModel,
                                        onPlantClick = { plantId ->
                                            // Optional lookup
                                        }
                                    )

                                    MainTab.ASSISTANT -> AiAssistantScreen(
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }
                    }
                }

                // Global Water Action Dialog
                if (plantForWaterDialog != null) {
                    WaterActionDialog(
                        plantName = plantForWaterDialog!!.name,
                        onDismiss = { plantForWaterDialog = null },
                        onConfirm = { moisture, notes ->
                            viewModel.markWatered(plantForWaterDialog!!.id, moisture, notes)
                            plantForWaterDialog = null
                        }
                    )
                }

                // Global Add Plant Dialog
                if (showAddPlantDialog) {
                    AddEditPlantDialog(
                        initialPlant = null,
                        onDismiss = { showAddPlantDialog = false },
                        onSave = { newPlant ->
                            viewModel.savePlant(newPlant)
                            showAddPlantDialog = false
                        }
                    )
                }

                // Firebase Setup Instructions Dialog
                if (showFirebaseInstructionsDialog) {
                    FirebaseInstructionsDialog(
                        onDismiss = { showFirebaseInstructionsDialog = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    title: String,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
