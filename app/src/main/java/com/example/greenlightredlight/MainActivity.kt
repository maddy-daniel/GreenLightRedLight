package com.example.greenlightredlight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.greenlightredlight.ui.theme.GreenLightRedLightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreenLightRedLightTheme {
                Surface(modifier = Modifier.fillMaxSize()){
                    val navController = rememberNavController()
                    val database = AppDatabase.getInstance(applicationContext)
                    val factory = ViewModelFactory(database.entryDao())
                    val viewModel: BudgetViewModel = viewModel(factory = factory)


                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.HOME
                    ){
                        composable(NavRoutes.HOME){
                            HomeScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(NavRoutes.ADD_ENTRY){
                            AddEntryScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(NavRoutes.DELETE_ENTRY){
                            DeleteEntryScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(NavRoutes.TAX_BREAKDOWN){
                            backStackEntry->
                            val entryId = backStackEntry.arguments?.getString("entryId")?.toIntOrNull() ?:0
                            PlaceholderScreen(navController = navController, title = "Tax Breakdown - Coming Soon")
                        }
                        composable(NavRoutes.ALL_TAX_BREAKDOWN){
                            PlaceholderScreen(navController = navController, title = "All Tax Breakdown - Coming Soon")
                        }
                    }
                }
            }
        }
    }
}