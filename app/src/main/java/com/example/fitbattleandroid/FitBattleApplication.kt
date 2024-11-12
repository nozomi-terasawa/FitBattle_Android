package com.example.fitbattleandroid

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import com.example.fitbattleandroid.notification.createNotificationChannels
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    var userToken: String? = null

    override fun onCreate() {
        super.onCreate()
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(
                NotificationManager::class.java,
            ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels(notificationManager)
        }
    }
}
