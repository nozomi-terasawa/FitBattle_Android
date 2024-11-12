package com.example.fitbattleandroid.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.fitbattleandroid.worker.SaveFitnessDataWorker

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        when (intent.action) {
            "SAVE_CALORIE" -> {
                val saveWorkRequest = OneTimeWorkRequestBuilder<SaveFitnessDataWorker>().build()
                WorkManager.getInstance(context).enqueue(saveWorkRequest)
            }
        }
    }
}
