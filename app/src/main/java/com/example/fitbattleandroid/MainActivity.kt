package com.example.fitbattleandroid

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.lifecycle.lifecycleScope
import com.example.fitbattleandroid.ui.navigation.App
import com.example.fitbattleandroid.ui.screen.isBackgroundLocationPermissionGranted
import com.example.fitbattleandroid.ui.theme.FitBattleAndroidTheme
import com.example.fitbattleandroid.viewmodel.MapViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"
private const val PERMISSION_SETTING_TAG = "PermissionSetting"

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_CHECK_SETTINGS = 1
    }

    private val providerPackageName: String = "com.google.android.apps.healthdata"
    private val permissions =
        setOf(
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        )
    private val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()
    private val requestPermissions =
        registerForActivityResult(requestPermissionActivityContract) { isGranted ->
            if (isGranted.containsAll(permissions)) {
                // パーミッションが許可された場合の処理
            } else {
                // パーミッションが許可されなかった場合の処理
            }
        }

    private val mapViewModel: MapViewModel by viewModels()
    private val backgroundPermissionGranted = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ヘルスコネクトがインストールされているかチェック
        val availabilityStatus = HealthConnectClient.getSdkStatus(this, providerPackageName)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            return
        }
        // TODO Android8以前のデバイスにはヘルスコネクトのインストールを要求しない
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            val uriString =
                "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
            this.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setPackage("com.android.vending")
                    data = Uri.parse(uriString)
                    putExtra("overlay", true)
                    putExtra("callerId", packageName)
                },
            )
            return
        }
        val healthConnectClient = HealthConnectClient.getOrCreate(this)

        // 権限のリクエスト
        lifecycleScope.launch {
            checkPermissionsAndRun(healthConnectClient)
        }

        // 位置情報の設定を確認
        checkLocationSettings(mapViewModel)

        enableEdgeToEdge()
        setContent {
            FitBattleAndroidTheme {
                App(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    mapViewModel = mapViewModel,
                    healthConnectClient = healthConnectClient,
                )
            }
        }
    }

    private suspend fun checkPermissionsAndRun(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(permissions)) {
            // 権限はすでに付与されているため、データの挿入または読み取りを続行する
        } else {
            requestPermissions.launch(permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        backgroundPermissionGranted.value = isBackgroundLocationPermissionGranted(this)

        /* 位置情報の更新を開始
        // TODO こっちの位置情報を使用して現在地をトラッキングする
        locationViewModel.startLocationUpdates()
         */
    }

    override fun onPause() {
        super.onPause()

        /* 位置情報の更新を停止
        locationViewModel.stopLocationUpdates()
         */
    }

    private fun checkLocationSettings(mapViewModel: MapViewModel) {
        val builder =
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(mapViewModel.locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { response ->
            Log.d(
                PERMISSION_SETTING_TAG,
                "GPS Enabled: ${response.locationSettingsStates?.isGpsUsable}",
            ) // GPSが有効かどうか
            Log.d(
                PERMISSION_SETTING_TAG,
                "Network Location Enabled: ${response.locationSettingsStates?.isNetworkLocationUsable}",
            ) // ネットワーク位置情報が有効かどうか
            Log.d(
                PERMISSION_SETTING_TAG,
                "Ble Location Enabled: ${response.locationSettingsStates?.isBleUsable}",
            ) // BLE位置情報が有効かどうか
            Log.d(
                PERMISSION_SETTING_TAG,
                "Location Settings are satisfied: ${response.locationSettingsStates?.isLocationUsable}",
            ) // 位置情報が有効かどうか
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS,
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitBattleAndroidTheme {
        App()
    }
}

 */
