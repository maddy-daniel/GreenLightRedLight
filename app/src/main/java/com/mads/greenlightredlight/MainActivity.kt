package com.mads.greenlightredlight

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.content.ContextCompat
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mads.greenlightredlight.ui.GreenLightRedLightTheme
private const val TAG = "WelcomeScreenDebug"
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate called. savedInstanceState= $savedInstanceState(null means fresh process start)")

        enableEdgeToEdge()
        setContent {
            GreenLightRedLightTheme {
                val navController = rememberNavController()
                val database = AppDatabase.getInstance(applicationContext)
                val factory = ViewModelFactory(database.entryDao(), applicationContext)
                val viewModel: BudgetViewModel = viewModel(factory = factory)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Log.d(TAG, "NavHost composing. currentRoute = $currentRoute")

                //App Lock: if enabled in Settings, the user must pass biometric authentication
                //before any screen content is shown.
                var isAuthenticated by rememberSaveable { mutableStateOf(!SecurePrefs.isLockEnabled(applicationContext)) }
                var authTrigger by remember { mutableStateOf(0) }

                LaunchedEffect(authTrigger) {
                    if (!isAuthenticated && SecurePrefs.isLockEnabled(applicationContext)) {
                        val biometricManager = BiometricManager.from(this@MainActivity)
                        val canAuthenticate
                        = biometricManager.canAuthenticate(
                            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        )
                        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
                            val executor = ContextCompat.getMainExecutor(this@MainActivity)
                            val biometricPrompt = BiometricPrompt(
                                this@MainActivity,
                                executor,
                                object : BiometricPrompt.AuthenticationCallback() {
                                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                        super.onAuthenticationSucceeded(result)
                                        isAuthenticated = true
                                        Log.d(TAG, "Biometric  auth succeeded")
                                    }

                                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                        super.onAuthenticationError(errorCode, errString)
                                        Log.d(TAG, "Biometric auth failed")
                                    }
                                }
                            )
                            val promptInfo =
                                BiometricPrompt.PromptInfo.Builder().setTitle("Unlock Green Light Red Light")
                                    .setSubtitle("Authenticate to view your financial data").setAllowedAuthenticators(
                                        BiometricManager.Authenticators.BIOMETRIC_WEAK
                                                or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                                    ).build()
                            biometricPrompt.authenticate(promptInfo)
                        } else {
                            //No biometric/PIN set up on this device - don't lock the user out.
                            Log.d(TAG, "No biometric/device credential available, skipping lock")
                            isAuthenticated = true
                        }
                    }
                }
                //Show the Welcome screen every time the app is opened, not just on the very first launch.
                //startDestination = WELCOME already handles the first launch, so this only needs to act
                // on later ON_START events.
                var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
                var isTransitioning by rememberSaveable { mutableStateOf(false) }
                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_STOP -> {
                                //Cover the screen before background so Android's task-switcher
                                //snapshot captures the cover, not the last real screen (e.g. Home)
                                if (!isFirstLaunch) {
                                    isTransitioning = true
                                    Log.d(TAG, "ON_STOP: covering screen before backgrounding")
                                }
                            }

                            Lifecycle.Event.ON_START -> {
                                if (isFirstLaunch) {
                                    isFirstLaunch = false
                                    Log.d(TAG, "ON_START: first launch, startDestination already showing WELCOME")
                                } else {
                                    isTransitioning = true
                                    Log.d(TAG, "ON_START: app reopened, navigating back to WELCOME")

                                    navController.navigate(NavRoutes.WELCOME) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                    isTransitioning = false
                                    Log.d(TAG, "Cover cleared after navigating to WELCOME")
                                }
                                if (SecurePrefs.isLockEnabled(applicationContext)) {
                                    isAuthenticated = false
                                    authTrigger++
                                }
                            }

                            else -> {}
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                val bottomNavRoutes = listOf(
                    NavRoutes.HOME,
                    NavRoutes.HISTORY,
                    NavRoutes.CALENDAR
                )
                if (!isAuthenticated) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = NavyBackground
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Text(
                                text = "\uD83D\uDD12",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "App Locked",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Authenticate to view your financial data",
                                color = MutedText,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {authTrigger++},
                                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                                shape = RoundedCornerShape(12.dp)
                            )
                            {
                                Text(
                                    text = "Unlock",
                                    color = NavyBackground,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                } else {

                    Box(modifier = Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)
                            //Should now show navigation bar and top bar of phone
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

                            )
                            { paddingValues ->
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
                                    composable(
                                        route = NavRoutes.ADD_ENTRY,
                                        arguments = listOf(
                                            androidx.navigation.navArgument("entryId"){
                                                type = androidx.navigation.NavType.StringType
                                                nullable = true
                                                defaultValue = null
                                            }
                                        )
                                    ) { backStackEntry ->
                                        val entryId = backStackEntry.arguments?.getString("entryId")?.toIntOrNull()
                                        AddEntryScreen(
                                            navController = navController,
                                            viewModel = viewModel,
                                            entryId = entryId
                                        )
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
                                    composable(NavRoutes.SETTINGS) {
                                        SettingsScreen(navController = navController)
                                    }
                                }
                            }
                        }

                        if (isTransitioning) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = NavyBackground
                            ) {}
                        }
                    }
                }
            }
        }
    }
    override fun onStart(){
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop called (app backgrounded, process may or may not survive")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called  (Activity destroyed)")
    }

}