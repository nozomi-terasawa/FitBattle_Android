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

    val locationPermissionState = mapViewModel.locationPermissionState.collectAsState().value
    val permissionDialogState = mapViewModel.permissionDialogState.collectAsState().value

    val deviceOrientationProvider = DeviceOrientationProvider(context)
    val heading = deviceOrientationProvider.heading

    val cameraBearing = rememberCameraPositionState()

    LaunchedEffect(cameraBearing) {
        Log.d("result", cameraBearing.toString() + "向き")
    }

    Header(
        content = {
            if (locationPermissionState.accessCoarseLocationState ||
                locationPermissionState.accessFineLocationState
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
                            val accessFineLocationState =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                ) == PackageManager.PERMISSION_GRANTED

                            mapViewModel.updateAccessFineLocationState(accessFineLocationState)

                            mapViewModel.showRequestBackgroundPermissionDialog(true)
                        }

                        permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                            // おおよその位置情報の権限が許可されました
                            val accessCoarseLocationState =
                                ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ) == PackageManager.PERMISSION_GRANTED

                            mapViewModel.updateAccessCoarseLocationState(accessCoarseLocationState)

                            mapViewModel.showUpgradeToPreciseLocationDialog(true)
                        }

                        else -> {
                            Log.d(TAG, "どちらの位置情報権限も拒否されました")
                            mapViewModel.showRequestLocationPermissionDialog(true)
                        }
                    }
                }

            // 位置情報の更新
            LaunchedEffect(Unit) {
                // おおよその位置情報
                val accessCoarseLocationState =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
                mapViewModel.updateAccessCoarseLocationState(accessCoarseLocationState)

                // 正確な位置情報
                val accessFineLocationState =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
                mapViewModel.updateAccessFineLocationState(accessFineLocationState)

                Log.d("result", "位置情報の更新: $locationPermissionState")

                // バックグラウンドでの位置情報
                val backgroundPermissionGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED

                mapViewModel.updateBackgroundPermissionState(backgroundPermissionGranted)

                val locationPriority =
                    if (locationPermissionState.accessFineLocationState) {
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
            if (permissionDialogState.showRequestLocationPermissionDialog) {
                RequestLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.showRequestLocationPermissionDialog(boolean)
                    },
                )
            }

            // システムダイアログでおおよその位置情報の権限が許可された時に表示するダイアログ
            if (permissionDialogState.showUpgradeToPreciseLocationDialog) {
                UpdateLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.showUpgradeToPreciseLocationDialog(boolean)
                    },
                )
            }

            // システムダイアログでバックグラウンドの権限がを求めるダイアログ
            if (permissionDialogState.showRequestBackgroundPermissionDialog &&
                !locationPermissionState.backgroundPermissionGranted
            ) {
                RequestBackgroundLocationPermissionDialog(
                    openDialog = true,
                    setShowDialog = { boolean ->
                        mapViewModel.showRequestBackgroundPermissionDialog(boolean)
                    },
                )
            }

            // 位置情報の取得が許可されている場合、端末の向きを取得
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                if (locationPermissionState.accessFineLocationState) {
                    deviceOrientationProvider.start()
                }
            }

            if (locationPermissionState.accessFineLocationState) {
                deviceOrientationProvider.start()
            }

            LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                deviceOrientationProvider.stop()
            }

            Button(
                onClick = {
                    if (locationPermissionState.backgroundPermissionGranted) {
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
                permissionState =
                    locationPermissionState.accessFineLocationState ||
                        locationPermissionState.accessCoarseLocationState,
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
    permissionState: Boolean,
    orientation: StateFlow<Float>,
) {
    var mapProperties = MapProperties()
    val currentLocation = locationDataFlow.collectAsState().value
    val cameraPosition =
        rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 15f)
        }

    LaunchedEffect(currentLocation) {
        cameraPosition.position = CameraPosition.fromLatLngZoom(LatLng(currentLocation.latitude, currentLocation.longitude), 15f)
    }

//    if (permissionState) {
//        mapProperties =
//            MapProperties(
//                isMyLocationEnabled = true,
//            )
//    }

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
