package com.example.fitbattleandroid.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.example.fitbattleandroid.MyApplication
import com.example.fitbattleandroid.receiver.AlarmReceiver
import java.util.Calendar

class AlarmViewModel(
    application: MyApplication,
) : AndroidViewModel(application) {
    private val context = application.applicationContext as MyApplication
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun setAlarmToSaveCalorie() {
        val intent =
            Intent(context, AlarmReceiver::class.java).apply {
                action = "SAVE_CALORIE"
            }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

//        //テスト
//        alarmManager.set(
//            AlarmManager.ELAPSED_REALTIME_WAKEUP,
//            SystemClock.elapsedRealtime() + 5 * 1000,
//            pendingIntent,
//        )

        val calendar: Calendar =
            Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 24)
            }

        // 毎日24時にアラームをセット
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    fun cancelAlarmToSaveCalorie() {
        val intent =
            Intent(context, AlarmReceiver::class.java).apply {
                action = "SAVE_CALORIE"
            }
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.cancel(pendingIntent)
    }
}
