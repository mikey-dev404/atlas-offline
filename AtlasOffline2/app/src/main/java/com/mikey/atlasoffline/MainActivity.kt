package com.mikey.atlasoffline

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp  // ADD THIS LINE
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.DirectionsRun




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ENABLE 120Hz HIGH REFRESH RATE
        enableHighRefreshRate()

        try {
            val database = AtlasDatabase.getDatabase(applicationContext)
            val dao = database.workoutDao()

            setContent {
                AtlasApp(dao)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun enableHighRefreshRate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display
                } else {
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay
                }

                val highestMode = display?.supportedModes?.maxByOrNull { it.refreshRate }
                highestMode?.let { mode ->
                    window.attributes = window.attributes.apply {
                        preferredDisplayModeId = mode.modeId
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun AtlasApp(dao: WorkoutDao) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SplashScreen(onEnter = { showSplash = false })
    } else {
        val viewModel: AtlasViewModel = viewModel(
            factory = AtlasViewModelFactory(dao)
        )
        MainScreen(viewModel)
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Stats : Screen("stats", "Stats", Icons.Default.BarChart)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Templates : Screen("templates", "Templates", Icons.Default.FolderCopy)
    object More : Screen("more", "More", Icons.Default.MoreHoriz)
    object Workout : Screen("workout", "Workout", Icons.Default.FitnessCenter)
    object Measurements : Screen("measurements", "Measurements", Icons.Default.MonitorWeight)
    object PlateCalc : Screen("platecalc", "Plate Calculator", Icons.Default.Calculate)
    object Cardio : Screen("cardio", "Cardio", Icons.Default.DirectionsRun)
}


@Composable
fun MainScreen(viewModel: AtlasViewModel) {
    val navController = rememberNavController()
    val currentSessionId by viewModel.currentSessionId.collectAsState()

    val items = listOf(
        Screen.Home,
        Screen.Stats,
        Screen.Calendar,
        Screen.Cardio,
        Screen.More
    )

    Scaffold(
        bottomBar = {
            if (currentSessionId == null) {
                NavigationBar(
                    containerColor = AtlasColors.Background
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = { Text(screen.title, fontSize = 11.sp) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = AtlasColors.TextSecondary,
                                unselectedTextColor = AtlasColors.TextSecondary,
                                indicatorColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = AtlasColors.Background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel, navController = navController)
            }

            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }

            composable(Screen.Calendar.route) {
                WorkoutCalendarScreen(viewModel = viewModel, navController = navController)
            }

            composable(Screen.Templates.route) {
                TemplatesScreen(viewModel = viewModel, navController = navController)
            }

            composable(Screen.More.route) {
                MoreScreen(viewModel = viewModel, navController = navController)
            }

            composable(Screen.Workout.route) {
                WorkoutScreen(viewModel = viewModel, navController = navController)
            }

            composable(Screen.Measurements.route) {
                MeasurementsScreen(viewModel = viewModel)
            }

            composable(Screen.PlateCalc.route) {
                PlateCalculatorScreen()
            }

            // NEW: Cardio screen route
            composable(Screen.Cardio.route) {
                CardioScreen(viewModel = viewModel)
            }


        }

    }
}
