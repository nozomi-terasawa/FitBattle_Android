package com.example.fitbattleandroid.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.fitbattleandroid.R

@RequiresApi(Build.VERSION_CODES.O)
fun sendGeofenceNotification(
    context: Context,
    notificationManager: NotificationManager,
    contentTitle: String,
    contentText: String,
) {
    val geofenceNotification =
        Notification
            .Builder(context, CHANNEL_ID_HIGH_PRIORITY)
            .setSmallIcon(R.drawable.baseline_swap_horizontal_circle_24)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_EVENT)
            .build()
    notificationManager.notify(1, geofenceNotification)
}
