package com.example.fitbattleandroid.ui.navigation

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitbattleandroid.ui.screen.EncounterHistoryScreen
import com.example.fitbattleandroid.ui.screen.FitnessMemory
import com.example.fitbattleandroid.ui.screen.LoginScreen
import com.example.fitbattleandroid.ui.screen.MapScreen
import com.example.fitbattleandroid.ui.screen.RegistrationScreen
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.example.fitbattleandroid.viewmodel.GeofencingClientViewModel
import com.example.fitbattleandroid.viewmodel.HealthConnectViewModel
import com.example.fitbattleandroid.viewmodel.HealthDataApiViewModel
import com.example.fitbattleandroid.viewmodel.LocationViewModel
import com.websarva.wings.android.myapplication.TopScreen

sealed class Screen(
    val route: String,
    val title: String,
) {
    data object Map : Screen("map", "Map")

    data object MyData : Screen("my-data", "MyData")

    data object EncounterList : Screen("encounter-list", "EncounterList")

    data object Top : Screen("top", "Top")

    data object Login : Screen("login", "Login")

    data object Regi : Screen("regi", "Regi")
}

val items =
    listOf(
        Screen.Map,
        Screen.MyData,
        Screen.EncounterList,
    )

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun App(
    modifier: Modifier,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    locationViewModel: LocationViewModel,
    geofenceViewModel: GeofencingClientViewModel = viewModel(),
    dataApiViewModel: HealthDataApiViewModel = viewModel(),
    backgroundPermissionGranted: MutableState<Boolean>,
    healthConnectClient: HealthConnectClient,
) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = Screen.Top.route,
    ) {
        composable(Screen.Top.route) { TopScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Regi.route) { RegistrationScreen(navController) }
        composable("main") {
            MainNavigation(
                requestPermissionLauncher,
                locationViewModel,
                geofenceViewModel,
                dataApiViewModel,
                backgroundPermissionGranted,
                healthConnectClient,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainNavigation(
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    locationViewModel: LocationViewModel,
    geofenceViewModel: GeofencingClientViewModel,
    dataAPIViewModel: HealthDataApiViewModel,
    backgroundPermissionGranted: MutableState<Boolean>,
    healthConnectClient: HealthConnectClient,
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.navigationBarsPadding(),
                backgroundColor = primaryContainerDarkMediumContrast,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = {
                            when (screen.route) {
                                "map" -> {
                                    Icon(
                                        Icons.Outlined.LocationOn,
                                        contentDescription = "Map",
                                    )
                                }
                                "encounter-list" -> {
                                    Icon(
                                        Icons.Outlined.List,
                                        contentDescription = "EncounterList",
                                    )
                                }
                                "my-data" -> {
                                    Icon(
                                        Icons.Outlined.AccountCircle,
                                        contentDescription = "MyData",
                                    )
                                }
                            }
                        },
                        label = { Text(screen.title) },
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
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Map.route,
            Modifier.padding(innerPadding),
        ) {
            composable(Screen.Map.route) {
                MapScreen(
                    Modifier.padding(innerPadding),
                    requestPermissionLauncher,
                    locationViewModel,
                    geofenceViewModel,
                    backgroundPermissionGranted,
                    healthDataApiViewModel = dataAPIViewModel,
                )
            }
            composable(Screen.MyData.route) {
                FitnessMemory(
                    modifier = Modifier,
                    healthConnectClient,
                    calorieViewModel = HealthConnectViewModel(), // TODO みろ！
                )
            }
            composable(Screen.EncounterList.route) {
                val encounterHistoryList by dataAPIViewModel.encounterMembers.collectAsState()
                EncounterHistoryScreen(
                    modifier = Modifier,
                    list = encounterHistoryList,
                    // encounterHistoryList,
                )
            }
        }
    }
}
