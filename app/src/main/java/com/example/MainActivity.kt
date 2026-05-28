package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.presentation.about.AboutScreen
import com.example.presentation.amortization.AmortizationScreen
import com.example.presentation.calculator.CalculatorScreen
import com.example.presentation.calculator.CalculatorViewModel
import com.example.presentation.comparison.ComparisonScreen
import com.example.presentation.components.BottomNavigationBar
import com.example.presentation.history.HistoryScreen
import com.example.presentation.onboarding.OnboardingScreen
import com.example.presentation.result.ResultScreen
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            // Instantiate the ViewModel using our custom Factory
            val calcViewModel: CalculatorViewModel = viewModel(factory = CalculatorViewModel.Factory)
            
            // Observe the chosen theme preference dynamically
            val appThemePreference by calcViewModel.appTheme.collectAsState()
            
            MyApplicationTheme(themePreference = appThemePreference) {
                val navController = rememberNavController()
                
                // Track current route to conditionally show or hide bottom bar
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "splash"
                
                // Bottom Bar should only render on root navigation destinations
                val showBottomBar = currentRoute in listOf("home", "history", "comparison", "settings")
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        // Pop up to the start destination to avoid building a huge stack
                                        popUpTo("home") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Splash Route
                        composable("splash") {
                            SplashScreen(
                                viewModel = calcViewModel,
                                onNavigateToOnboarding = {
                                    navController.navigate("onboarding") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                },
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        // 2. Onboarding Route
                        composable("onboarding") {
                            OnboardingScreen(
                                viewModel = calcViewModel,
                                onFinishOnboarding = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        // 3. Home / EMI Calculator Route
                        composable("home") {
                            CalculatorScreen(
                                viewModel = calcViewModel,
                                onNavigateToResult = {
                                    navController.navigate("result")
                                }
                            )
                        }
                        
                        // 4. Result Route
                        composable("result") {
                            ResultScreen(
                                viewModel = calcViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onNavigateToAmortization = {
                                    navController.navigate("amortization")
                                },
                                onNavigateToCompare = {
                                    navController.navigate("comparison")
                                }
                            )
                        }
                        
                        // 5. Amortization Route
                        composable("amortization") {
                            AmortizationScreen(
                                viewModel = calcViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        
                        // 6. History Route
                        composable("history") {
                            HistoryScreen(
                                viewModel = calcViewModel,
                                onNavigateToResult = {
                                    navController.navigate("result")
                                }
                            )
                        }
                        
                        // 7. Comparison Route
                        composable("comparison") {
                            ComparisonScreen(
                                viewModel = calcViewModel
                            )
                        }
                        
                        // 8. Settings Route
                        composable("settings") {
                            SettingsScreen(
                                viewModel = calcViewModel,
                                onNavigateToAbout = {
                                    navController.navigate("about")
                                }
                            )
                        }
                        
                        // 9. About Route
                        composable("about") {
                            AboutScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
