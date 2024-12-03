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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.model.LocationData
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.dialog.RequestBackgroundLocationPermissionDialog
import com.example.fitbattleandroid.ui.dialog.RequestLocationPermissionDialog
import com.example.fitbattleandroid.ui.dialog.UpdateLocationPermissionDialog
import com.example.fitbattleandroid.ui.map.CurrentLocationMarker
import com.example.fitbattleandroid.ui.map.DeviceOrientationProvider
import com.example.fitbattleandroid.ui.state.PermissionDialogState
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
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "MapScreen"

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MapScreen(
    modifier: Modifier,
    mapViewModel: MapViewModel,
    geofenceMapViewModel: GeofenceMapViewModel,
) {
    val locationDataFlow = mapViewModel.location
    val context = LocalContext.current
    val geofenceList = mapViewModel.geofenceList
    val scope = rememberCoroutineScope()
    val mapScreenUiState = mapViewModel.mapScreenUiState.collectAsState().value
    val deviceOrientationProvider = DeviceOrientationProvider(context)
    val heading = deviceOrientationProvider.heading

    Header(
        content = {
            if (mapScreenUiState.locationPermissionState.accessCoarseLocationState ||
                mapScreenUiState.locationPermissionState.accessFineLocationState
            ) {
                LaunchedEffect(Unit) {
                    scope.launch(Dispatchers.IO) {
                        mapViewModel.fetchLocation()
                    }
                }
            }

            // ランチャー起動後のコールバック
            val locationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                ) { permissions ->
                    when {
                        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                            // 正確な位置情報の権限が許可
                            mapViewModel.checkFineLocationPermission(context)
                            mapViewModel.updatePermissionDialogState(PermissionDialogState::showRequestBackgroundPermissionDialog, true)
                        }

                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            // おおよその位置情報の権限が許可
                            mapViewModel.checkCoarseLocationPermission(context)
                            mapViewModel.updatePermissionDialogState(PermissionDialogState::showUpgradeToPreciseLocationDialog, true)
                        }

                        else -> {
                            Log.d(TAG, "どちらの位置情報権限も拒否されました")
                            mapViewModel.updatePermissionDialogState(PermissionDialogState::showRequestLocationPermissionDialog, true)
                        }
                    }
                }

            // 位置情報の更新
            LaunchedEffect(Unit) {
                val locationPriority =
                    if (mapScreenUiState.locationPermissionState.accessFineLocationState) {
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
            if (mapScreenUiState.permissionDialogState.showRequestLocationPermissionDialog) {
                RequestLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.updatePermissionDialogState(PermissionDialogState::showRequestLocationPermissionDialog, boolean)
                    },
                )
            }

            // システムダイアログでおおよその位置情報の権限が許可された時に表示するダイアログ
            if (mapScreenUiState.permissionDialogState.showUpgradeToPreciseLocationDialog) {
                UpdateLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.updatePermissionDialogState(PermissionDialogState::showUpgradeToPreciseLocationDialog, boolean)
                    },
                )
            }

            // システムダイアログでバックグラウンドの権限がを求めるダイアログ
            if (mapScreenUiState.permissionDialogState.showRequestBackgroundPermissionDialog &&
                !mapScreenUiState.locationPermissionState.backgroundPermissionGranted
            ) {
                RequestBackgroundLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.updatePermissionDialogState(PermissionDialogState::showRequestBackgroundPermissionDialog, boolean)
                    },
                )
            }

            // 位置情報の取得が許可されている場合、端末の向きを取得
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                // 位置情報の権限のチェックと状態の更新
                mapViewModel.checkCoarseLocationPermission(context)
                mapViewModel.checkFineLocationPermission(context)
                Log.d("result", "onResume")
                mapViewModel.checkBackgroundLocationPermission(context)

                if (mapScreenUiState.locationPermissionState.accessFineLocationState) {
                    deviceOrientationProvider.start()
                }
            }

            if (mapScreenUiState.locationPermissionState.accessFineLocationState) {
                deviceOrientationProvider.start()
            }

            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                deviceOrientationProvider.stop()
            }

            Button(
                onClick = {
                    if (mapScreenUiState.locationPermissionState.backgroundPermissionGranted) {
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
                locationDataFlow = locationDataFlow,
                geofenceList = geofenceList.toList(),
                orientation = heading,
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
    locationDataFlow: StateFlow<LocationData>,
    geofenceList: List<Geofence>,
    orientation: StateFlow<Float>,
) {
    val mapProperties = MapProperties()
    val currentLocation = locationDataFlow.collectAsState().value
    val cameraPosition =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 15f)
        }

    LaunchedEffect(currentLocation) {
        cameraPosition.position = CameraPosition.fromLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPosition,
        properties = mapProperties,
    ) {
        // デバイスの向きとカメラのベアリングを組み合わせた合成ベアリング
        val deviceOrientation = orientation.collectAsState().value
        val cameraBearing by remember {
            derivedStateOf { cameraPosition.position.bearing }
        }
        val combinedBearing = (deviceOrientation - cameraBearing + 360) % 360 // 0〜360°に正規化

        CustomMarker(
            bearing = combinedBearing,
            markerState =
                MarkerState(
                    position = LatLng(currentLocation.latitude, currentLocation.longitude),
                ),
        )

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

@Composable
fun CustomMarker(
    bearing: Float,
    markerState: MarkerState,
) {
    MarkerComposable(
        state = markerState,
        rotation = bearing,
        anchor = Offset(0.5f, 0.5f), // マーカーの基準点を中央下部に設定
    ) {
        CurrentLocationMarker()
    }
}
