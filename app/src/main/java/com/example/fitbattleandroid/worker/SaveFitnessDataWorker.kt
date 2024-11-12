package com.example.fitbattleandroid.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitbattleandroid.MyApplication
import com.example.fitbattleandroid.data.FitnessRemoteDataSource
import com.example.fitbattleandroid.data.remote.SaveFitnessReq
import com.example.fitbattleandroid.repositoryImpl.SaveFitnessRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveFitnessDataWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val userToken = (context as MyApplication).userToken
    private val saveFitnessRepository =
        SaveFitnessRepositoryImpl(
            fitnessRemoteDataSource = FitnessRemoteDataSource(),
        )

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                if (userToken == null) {
                    Log.d("result", "userToken is null")
                    return@withContext Result.failure()
                } else {
                    saveFitnessRepository.saveFitnessData(
                        request =
                            SaveFitnessReq(
                                userId = 12,
                                calories = 100.0.toFloat(),
                                timestamp = "2021-10-01T10:00:00.391Z",
                            ),
                        userToken = userToken,
                    )
                    return@withContext Result.success()
                }
            } catch (throwable: Throwable) {
                Log.d("result", throwable.toString())
                return@withContext Result.failure()
            }
        }
    }
}
