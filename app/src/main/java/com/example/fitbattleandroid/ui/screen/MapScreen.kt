package com.example.fitbattleandroid.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.model.LocationData
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.dialog.RequestBackgroundLocationPermissionDialog
import com.example.fitbattleandroid.ui.dialog.RequestLocationPermissionDialog
import com.example.fitbattleandroid.ui.dialog.UpdateLocationPermissionDialog
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.example.fitbattleandroid.viewmodel.GeofenceMapViewModel
import com.example.fitbattleandroid.viewmodel.MapViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MapScreen"

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MapScreen(
    modifier: Modifier,
    mapViewModel: MapViewModel,
    geofenceMapViewModel: GeofenceMapViewModel,
) {
    val locationData = mapViewModel.location.collectAsState().value
    val context = LocalContext.current
    val geofenceList = mapViewModel.geofenceList
    val scope = rememberCoroutineScope()
    val currentLocation = remember { mutableStateOf(locationData) }
    val showRequestLocationPermissionDialog = remember { mutableStateOf(false) } // 位置情報権限のリクエストダイアログ
    val showUpgradeToPreciseLocationDialog = remember { mutableStateOf(false) } // 正確な位置情報のリクエストダイアログ
    val showRequestBackgroundPermissionDialog = remember { mutableStateOf(false) } // バックグラウンドの位置情報権限のリクエストダイアログ

    val accessFineLocationState = remember { mutableStateOf(false) }
    val accessCoarseLocationState = remember { mutableStateOf(false) }
    val backgroundPermissionGranted = remember { mutableStateOf(false) }

    Header(
        content = {
            if (accessCoarseLocationState.value || accessFineLocationState.value) {
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        currentLocation.value = mapViewModel.fetchLocation()
                    }
                }
            }

            val locationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                ) { permissions ->
                    when {
                        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                            Log.d(TAG, "正確な位置情報の権限が許可されました")
                            showRequestBackgroundPermissionDialog.value = true
                        }

                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            Log.d(TAG, "おおよその位置情報の権限が許可されました")
                            showUpgradeToPreciseLocationDialog.value = true
                        }

                        else -> {
                            Log.d(TAG, "どちらの位置情報権限も拒否されました")
                            showRequestLocationPermissionDialog.value = true
                        }
                    }
                }

            LaunchedEffect(Unit) {
                accessCoarseLocationState.value =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED

                accessFineLocationState.value =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED

                backgroundPermissionGranted.value =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED

                val locationPriority =
                    if (accessFineLocationState.value) {
                        Priority.PRIORITY_HIGH_ACCURACY
                    } else {
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY
                    }

                mapViewModel.updatePriority(locationPriority)

                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            }

            // TODO ダイアログはonResumeに移した方がいいかも？ReCompose時に毎回表示されてしまう
            // システムダイアログで権限を拒否された時に表示するダイアログ
            if (showRequestLocationPermissionDialog.value) {
                RequestLocationPermissionDialog(
                    openDialog = showRequestLocationPermissionDialog.value,
                    setShowDialog = { boolean ->
                        showRequestLocationPermissionDialog.value = boolean
                    },
                )
            }

            // システムダイアログでおおよその位置情報の権限が許可された時に表示するダイアログ
            if (showUpgradeToPreciseLocationDialog.value) {
                UpdateLocationPermissionDialog(
                    openDialog = showUpgradeToPreciseLocationDialog.value,
                    setShowDialog = { boolean ->
                        showUpgradeToPreciseLocationDialog.value = boolean
                    },
                )
            }

            // システムダイアログでバックグラウンドの権限がを求めるダイアログ
            if (showRequestBackgroundPermissionDialog.value && !backgroundPermissionGranted.value) {
                RequestBackgroundLocationPermissionDialog(
                    openDialog = showRequestBackgroundPermissionDialog.value,
                    setShowDialog = { boolean ->
                        showRequestBackgroundPermissionDialog.value = boolean
                    },
                )
            }

            Button(
                onClick = {
                    if (backgroundPermissionGranted.value) {
                        mapViewModel.addGeofence()
                        mapViewModel.registerGeofence()
                        scope.launch(Dispatchers.IO) {
                            geofenceMapViewModel.sendGeoFenceEntryRequest(
                                EntryGeoFenceReq(
                                    userId = 12,
                                    geoFenceId = 2,
                                    entryTime = "2021-10-01T10:00:00.391Z",
                                ),
                            )
                        }
                    }
                },
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = primaryContainerDarkMediumContrast,
                    ),
            ) {
                Text(
                    text = "ジオフェンスを追加",
                    color = onPrimaryDark,
                )
            }

            ShowMap(
                modifier = Modifier.fillMaxSize(),
                locationData =
                    LocationData(
                        currentLocation.value.latitude,
                        currentLocation.value.longitude,
                        0,
                    ),
                geofenceList = geofenceList.toList(),
                permissionState = accessFineLocationState.value || accessCoarseLocationState.value,
            )
        },
        actions = {},
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
fun isBackgroundLocationPermissionGranted(context: Context): Boolean {
    // 位置情報取得権限が常に許可されているかチェック
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun ShowMap(
    modifier: Modifier,
    locationData: LocationData,
    geofenceList: List<Geofence>,
    permissionState: Boolean,
) {
    var mapProperties = MapProperties()
    val cameraPosition =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(locationData.latitude, locationData.longitude), 15f)
        }

    LaunchedEffect(locationData) {
        cameraPosition.position = CameraPosition.fromLatLngZoom(LatLng(locationData.latitude, locationData.longitude), 15f)
    }

    if (permissionState) {
        mapProperties =
            MapProperties(
                isMyLocationEnabled = true,
            )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPosition,
        properties = mapProperties,
    ) {
        geofenceList.forEach { geofence ->
            Circle(
                center = LatLng(geofence.latitude, geofence.longitude),
                radius = geofence.radius.toDouble(),
                fillColor = Color(0, 255, 0, 40),
                strokeColor = Color.Red,
                strokeWidth = 2f,
            )
        }
    }
}
