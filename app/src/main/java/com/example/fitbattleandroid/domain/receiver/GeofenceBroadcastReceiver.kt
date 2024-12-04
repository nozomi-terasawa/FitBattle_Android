package com.example.fitbattleandroid.domain.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.fitbattleandroid.data.api.dto.geofence.NewPassingInfo
import com.example.fitbattleandroid.data.repositoryImpl.WebSocketRepositoryImpl
import com.example.fitbattleandroid.ui.notification.sendGeofenceNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent?,
    ) {
        if (intent == null) {
            return
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            return
        }
        if (geofencingEvent.hasError()) {
            val errorMessage =
                GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
        ) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            val geofenceTransitionDetails =
                triggeringGeofences?.let {
                    getGeofenceTransitionDetails(
                        geofenceTransition,
                        it,
                    )
                }

            // WebSocket通信
            val webSocketRepository =
                WebSocketRepositoryImpl(
                    client =
                        HttpClient(CIO) {
                            install(WebSockets)
                        },
                )

            // WebSocket通信
            CoroutineScope(Dispatchers.IO).launch {
                webSocketRepository.connect {
                    for (message in incoming) {
                        when (message) {
                            is Frame.Text -> {
                                val text = message.readText()
                                val parsedMessage = Json.decodeFromString<NewPassingInfo>(text)
                                sendGeofenceNotification(
                                    context,
                                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager,
                                    "すれ違いました。",
                                    parsedMessage.message,
                                )
                            }
                            else -> { /*なにもしない*/ }
                        }
                    }
                }
            }

            sendGeofenceNotification(
                context,
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager,
                "ジオフェンス通知",
                "$geofenceTransitionDetails",
            )
        }
    }

    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: MutableList<Geofence>,
    ): String {
        val latestGeofenceIndex = triggeringGeofences.size - 1
        return when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "${triggeringGeofences[latestGeofenceIndex].requestId}の中に入りました"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "${triggeringGeofences[latestGeofenceIndex].requestId}から出ました"
            else -> "不明"
        }
    }
}
