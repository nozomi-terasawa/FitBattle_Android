package com.example.fitbattleandroid.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

const val CHANNEL_ID_HIGH_PRIORITY = "high_priority"

@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannels(notificationManager: NotificationManager) {
    val geofenceChannel =
        NotificationChannel(
            CHANNEL_ID_HIGH_PRIORITY,
            "ジオフェンスの通知",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "ジオフェンスの出入りの通知を許可"
            enableVibration(false)
        }
    notificationManager.createNotificationChannel(geofenceChannel)
}
