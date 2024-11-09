package com.example.fitbattleandroid.ui.navigation

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitbattleandroid.data.EncounterRemoteDatasource
import com.example.fitbattleandroid.data.FitnessRemoteDataSource
import com.example.fitbattleandroid.repositoryImpl.AuthRepositoryImpl
import com.example.fitbattleandroid.repositoryImpl.GeofenceEntryRepositoryImpl
import com.example.fitbattleandroid.repositoryImpl.SaveFitnessRepositoryImpl
import com.example.fitbattleandroid.ui.screen.EncounterHistoryScreen
import com.example.fitbattleandroid.ui.screen.FitnessMemory
import com.example.fitbattleandroid.ui.screen.LocationRationaleScreen
import com.example.fitbattleandroid.ui.screen.LoginScreen
import com.example.fitbattleandroid.ui.screen.MapScreen
import com.example.fitbattleandroid.ui.screen.RegistrationScreen
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.example.fitbattleandroid.viewmodel.AuthViewModel
import com.example.fitbattleandroid.viewmodel.HealthConnectViewModel
import com.example.fitbattleandroid.viewmodel.HealthDataApiViewModel
import com.example.fitbattleandroid.viewmodel.MapViewModel
import com.websarva.wings.android.myapplication.TopScreen

sealed class Screen(
    val route: String,
    val title: String,
) {
    data object LocationPermission : Screen("location-permission", "LocationPermission")

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
    mapViewModel: MapViewModel,
    backgroundPermissionGranted: MutableState<Boolean>,
    healthConnectClient: HealthConnectClient,
    context: Application = LocalContext.current.applicationContext as Application,
    authViewModel: AuthViewModel = AuthViewModel(context, AuthRepositoryImpl()),
) {
    val navController = rememberNavController()

    NavHost(
        navController,
        startDestination = Screen.Top.route,
    ) {
        val nextDestination =
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
            ) { // 正確な位置情報
                "main"
            } else {
                Screen.LocationPermission.route
            }

        composable(Screen.Top.route) { TopScreen(navController) }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateMain = {
                    navController.navigate(nextDestination)
                },
                onNavigateRegi = {
                    navController.navigate(Screen.Regi.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel,
            )
        }
        composable(Screen.Regi.route) {
            RegistrationScreen(
                onNavigateMain = {
                    navController.navigate(nextDestination)
                },
                onNavigateLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                authViewModel = authViewModel,
            )
        }
        composable(Screen.LocationPermission.route) {
            LocationRationaleScreen(
                modifier = modifier,
                onNavigateMapScreen = {
                    navController.navigate("main")
                },
            )
        }
        composable("main") {
            MainNavigation(
                mapViewModel,
                dataAPIViewModel =
                    viewModel {
                        HealthDataApiViewModel(GeofenceEntryRepositoryImpl(EncounterRemoteDatasource()))
                    },
                backgroundPermissionGranted,
                healthConnectClient,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MainNavigation(
    mapViewModel: MapViewModel,
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
                    mapViewModel,
                    backgroundPermissionGranted,
                    healthDataApiViewModel = dataAPIViewModel,
                )
            }
            composable(Screen.MyData.route) {
                FitnessMemory(
                    modifier = Modifier,
                    healthConnectClient,
                    calorieViewModel =
                        HealthConnectViewModel(
                            SaveFitnessRepositoryImpl(
                                FitnessRemoteDataSource(),
                            ),
                        ),
                )
            }
            composable(Screen.EncounterList.route) {
                val geofenceEntryState = dataAPIViewModel.geofenceEntryState.collectAsState().value

                EncounterHistoryScreen(
                    modifier = Modifier,
                    geofenceEntryState = geofenceEntryState,
                )
            }
        }
    }
}
