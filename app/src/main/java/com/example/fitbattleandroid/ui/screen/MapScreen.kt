package com.example.fitbattleandroid.ui.screen

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.model.LocationData
import com.example.fitbattleandroid.ui.permissioncheck.LocationPermissionRequest
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.example.fitbattleandroid.viewmodel.GeofenceMapViewModel
import com.example.fitbattleandroid.viewmodel.MapViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 省略

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun MapScreen(
    modifier: Modifier,
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    mapViewModel: MapViewModel,
    backgroundPermissionGranted: MutableState<Boolean>,
    geofenceMapViewModel: GeofenceMapViewModel,
) {
    val locationData = mapViewModel.location.collectAsState().value
    val geofenceList = mapViewModel.geofenceList
    val scope = rememberCoroutineScope()
    val currentLocation = remember { mutableStateOf(locationData) }
    val permissionGranted = remember { mutableStateOf(false) }

    if (permissionGranted.value) {
        LaunchedEffect(Unit) {
            scope.launch(Dispatchers.IO) {
                currentLocation.value = mapViewModel.fetchLocation()
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LocationPermissionRequest(
            requestPermissionLauncher = requestPermissionLauncher,
            fetchLocation = {
                scope.launch(Dispatchers.IO) {
                    mapViewModel.fetchLocation()
                }
            },
            updatePriority = { priority ->
                mapViewModel.updatePriority(priority)
                mapViewModel.updateLocationRequest()
            },
            onPermissionGranted = { boolean ->
                permissionGranted.value = boolean
            },
            backgroundPermissionGranted = backgroundPermissionGranted,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(primaryContainerDarkMediumContrast)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Share Fit",
                style =
                    MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = onPrimaryDark,
                    ),
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
            permissionState = permissionGranted.value,
        )
    }
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
