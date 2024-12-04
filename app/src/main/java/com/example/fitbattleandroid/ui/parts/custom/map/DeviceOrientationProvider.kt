package com.example.fitbattleandroid.ui.parts.custom.map

import android.content.Context
import com.google.android.gms.location.DeviceOrientationListener
import com.google.android.gms.location.DeviceOrientationRequest
import com.google.android.gms.location.FusedOrientationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class DeviceOrientationProvider(
    context: Context,
) {
    // FOP APIのクライアントを取得
    private val fusedOrientationProviderClient: FusedOrientationProviderClient =
        LocationServices.getFusedOrientationProviderClient(context)

    // 方向
    private var _heading = MutableStateFlow(0f)
    val heading: StateFlow<Float> get() = _heading

    private val listener =
        DeviceOrientationListener { orientation ->
            CoroutineScope(Dispatchers.IO).launch {
                _heading.emit(orientation.headingDegrees)
            }
        }

    private var executor = Executors.newSingleThreadExecutor()

    fun start() {
        // FOPリクエストを作成
        val request = DeviceOrientationRequest.Builder(DeviceOrientationRequest.OUTPUT_PERIOD_FAST).build()

        executor = Executors.newSingleThreadExecutor()

        fusedOrientationProviderClient
            .requestOrientationUpdates(request, executor, listener)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun stop() {
        fusedOrientationProviderClient.removeOrientationUpdates(listener)
        executor.shutdown()
    }
}
