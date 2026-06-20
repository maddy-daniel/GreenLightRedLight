package com.mads.greenlightredlight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.mads.greenlightredlight.ui.GreenLightRedLightTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreenLightRedLightTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getInstance(applicationContext)
                val factory = ViewModelFactory(database.entryDao(), applicationContext)
                val viewModel: BudgetViewModel = viewModel(factory = factory)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val bottomNavRoutes = listOf(
                    NavRoutes.HOME,
                    NavRoutes.HISTORY,
                    NavRoutes.CALENDAR
                )

                Surface(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)    //Should now show navigation bar and top bar of phone
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute in bottomNavRoutes) {
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onHomeClick = {
                                        navController.navigate(NavRoutes.HOME) {
                                            popUpTo(NavRoutes.HOME) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onHistoryClick = {
                                        navController.navigate(NavRoutes.HISTORY) {
                                            popUpTo(NavRoutes.HOME)
                                        }

                                    },
                                    onCalendarClick = {
                                        navController.navigate(NavRoutes.CALENDAR) {
                                            popUpTo(NavRoutes.HOME)
                                        }
                                    }
                                )

                            }

                        }

                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = NavRoutes.WELCOME,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(NavRoutes.WELCOME) {
                                WelcomeScreen(navController = navController)
                            }
                            composable(NavRoutes.HELP) {
                                HelpScreen(navController = navController)
                            }

                            composable(NavRoutes.HOME) {
                                HomeScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(NavRoutes.ADD_ENTRY) {
                                AddEntryScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(NavRoutes.DELETE_ENTRY) {
                                DeleteEntryScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(NavRoutes.TAX_BREAKDOWN) { backStackEntry ->
                                val entryId = backStackEntry.arguments?.getString("entryId")?.toIntOrNull() ?: 0
                                TaxBreakdownScreen(
                                    navController = navController,
                                    viewModel = viewModel,
                                    entryId = entryId
                                )
                            }
                            composable(NavRoutes.ALL_TAX_BREAKDOWN) {
                                AllTaxBreakdownScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(NavRoutes.CALENDAR) {
                                CalendarScreen(navController = navController, viewModel = viewModel)
                            }
                            composable(NavRoutes.HISTORY) {
                                HistoryScreen(navController = navController, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
