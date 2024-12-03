package com.example.fitbattleandroid.viewmodel

import android.Manifest
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitbattleandroid.MyApplication
import com.example.fitbattleandroid.model.GeofenceData
import com.example.fitbattleandroid.model.LocationData
import com.example.fitbattleandroid.receiver.GeofenceBroadcastReceiver
import com.example.fitbattleandroid.repository.FetchGeoFenceInfoRepository
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MapViewModel"

data class LocationPermissionState(
    var accessFineLocationState: Boolean = false,
    var accessCoarseLocationState: Boolean = false,
    var backgroundPermissionGranted: Boolean = false,
)

data class PermissionDialogState(
    var showRequestLocationPermissionDialog: Boolean = false, // 位置情報権限のリクエストダイアログ
    var showUpgradeToPreciseLocationDialog: Boolean = false, // 正確な位置情報のリクエストダイアログ
    var showRequestBackgroundPermissionDialog: Boolean = false, // バックグラウンドの位置情報権限のリクエストダイアログ
)

@HiltViewModel
class MapViewModel
    @Inject
    constructor(
        application: Application,
        private val fetchGeoFenceInfoRepository: FetchGeoFenceInfoRepository,
    ) : AndroidViewModel(application) {
        private val applicationContext = application.applicationContext

        // ユーザートークンがnullであることはありえないが、念のため
        private val userToken: String? by lazy {
            (getApplication() as MyApplication).userToken
        }

        // ジオフェンス
        private val geofencingClient: GeofencingClient =
            LocationServices.getGeofencingClient(applicationContext)
        private var _geofenceList = mutableStateListOf<Geofence>()
        val geofenceList: List<Geofence> = _geofenceList

        private var entry: MutableList<GeofenceData> = mutableListOf()

        // 位置情報
        private var _location =
            MutableStateFlow(
                LocationData(0.0, 0.0, Priority.PRIORITY_BALANCED_POWER_ACCURACY),
            )
        val location: StateFlow<LocationData> = _location.asStateFlow()
        private val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        private var _locationRequest: LocationRequest = createLocationRequest()
        val locationRequest: LocationRequest = _locationRequest
        private var isLocationUpdatesActive = false

        private val locationCallback =
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        val updatedLatitude = location.latitude
                        val updatedLongitude = location.longitude

                        _location.value =
                            _location.value.copy(
                                latitude = updatedLatitude,
                                longitude = updatedLongitude,
                            )
                    }
                }
            }

        // 位置情報の権限の状態
        private val _locationPermissionState = MutableStateFlow(LocationPermissionState())
        val locationPermissionState: StateFlow<LocationPermissionState> = _locationPermissionState.asStateFlow()

        // 正確な位置情報の更新
        fun updateAccessFineLocationState(state: Boolean) {
            _locationPermissionState.update {
                _locationPermissionState.value.copy(
                    accessFineLocationState = state,
                )
            }
        }

        // おおよその位置情報の更新
        fun updateAccessCoarseLocationState(state: Boolean) {
            _locationPermissionState.update {
                _locationPermissionState.value.copy(
                    accessCoarseLocationState = state,
                )
            }
        }

        // バックグラウンドでの位置情報の更新
        fun updateBackgroundPermissionState(state: Boolean) {
            _locationPermissionState.update {
                _locationPermissionState.value.copy(
                    backgroundPermissionGranted = state,
                )
            }
        }

        private val _permissionDialogState = MutableStateFlow(PermissionDialogState())
        val permissionDialogState: StateFlow<PermissionDialogState> = _permissionDialogState.asStateFlow()

        fun showRequestLocationPermissionDialog(state: Boolean) {
            _permissionDialogState.update {
                _permissionDialogState.value.copy(
                    showRequestLocationPermissionDialog = state,
                )
            }
        }

        fun showUpgradeToPreciseLocationDialog(state: Boolean) {
            _permissionDialogState.update {
                _permissionDialogState.value.copy(
                    showUpgradeToPreciseLocationDialog = state,
                )
            }
        }

        fun showRequestBackgroundPermissionDialog(state: Boolean) {
            _permissionDialogState.update {
                _permissionDialogState.value.copy(
                    showRequestBackgroundPermissionDialog = state,
                )
            }
        }

        // 位置情報の優先度の更新
        fun updatePriority(priority: Int) {
            _location.value =
                _location.value.copy(
                    priority = priority,
                )
            updateLocationRequest()
        /* priorityの確認
        Log.d("LocationViewModel", locationRequest.priority.toPriorityString())
         */
        }

        // 位置情報リクエストの更新
        // 優先度や更新頻度などを変更した際に、このメソッドを呼び出して更新完了
        private fun updateLocationRequest() {
            _locationRequest = createLocationRequest()
        }

        // 　位置情報リクエストの設定
        // 利用可能な設定：https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        private fun createLocationRequest(): LocationRequest =
            LocationRequest
                .Builder(1000)
                // .setPriority(_location.value.priority)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

        // 位置情報の取得
        // デバイスが最後に確認された場所の位置情報
        fun fetchLocation(): LocationData {
            try {
                val result =
                    Tasks.await(
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location -> // Log.d("result", "緯度: ${location?.latitude}, 経度: ${location?.longitude}")
                            },
                    )
                _location.value =
                    _location.value.copy(
                        latitude = result.latitude,
                        longitude = result.longitude,
                    )
            } catch (e: SecurityException) {
                // 権限が付与されていない場合
                Log.d(TAG, e.toString())
            }
            return _location.value
        }

        // 位置情報の更新を開始
        fun startLocationUpdates() {
            if (!isLocationUpdatesActive) {
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper(),
                    )
                    isLocationUpdatesActive = true
                } catch (e: SecurityException) {
                    Log.d(TAG, e.toString())
                }
            }
        }

        // 位置情報の更新を停止
        fun stopLocationUpdates() {
            if (isLocationUpdatesActive) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                isLocationUpdatesActive = false
            }
        }

        private val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(applicationContext, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(
                applicationContext,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
        }

        fun addGeofence() {
            if (userToken != null) {
                viewModelScope.launch {
                    entry =
                        fetchGeoFenceInfoRepository
                            .fetchGeoFenceInfo(userToken!!)
                            .geoFence
                            .map {
                                GeofenceData(
                                    requestId = it.name,
                                    longitude = it.longitude,
                                    latitude = it.latitude,
                                    radius = it.radius,
                                )
                            }.toMutableList()
                    entry.forEach { entry ->
                        _geofenceList.add(
                            Geofence
                                .Builder()
                                .setRequestId(entry.requestId)
                                .setCircularRegion(
                                    entry.latitude,
                                    entry.longitude,
                                    entry.radius,
                                ).setExpirationDuration(Geofence.NEVER_EXPIRE)
                                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                                // .setLoiteringDelay(60000)
                                .build(),
                        )
                    }
                }
            } else {
                // ユーザートークンがない場合は想定しない
                Log.d(TAG, "★:userToken is null")
            }
        }

        private fun getGeofencingRequest(): GeofencingRequest =
            GeofencingRequest
                .Builder()
                .apply {
                    setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    addGeofences(_geofenceList)
                }.build()

        // リストのジオフェンスを登録
        fun registerGeofence() {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            if (_geofenceList.isEmpty()) {
                return
            }

            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
                addOnSuccessListener {
                }
                addOnFailureListener { e ->
                    val errorCode = (e as? ApiException)?.statusCode
                    val errorMessage = GeofenceStatusCodes.getStatusCodeString(errorCode ?: -1)
                    Log.d(TAG, errorMessage)
                }
            }
        }
    }
