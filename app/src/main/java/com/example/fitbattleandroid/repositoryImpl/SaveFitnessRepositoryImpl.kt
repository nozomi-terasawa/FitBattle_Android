package com.example.fitbattleandroid.repositoryImpl

import com.example.fitbattleandroid.data.FitnessRemoteDataSource
import com.example.fitbattleandroid.data.remote.SaveFitnessReq

class SaveFitnessRepositoryImpl(
    private val fitnessRemoteDataSource: FitnessRemoteDataSource,
) {
    suspend fun saveFitnessData(
        request: SaveFitnessReq,
        userToken: String,
    ) {
        fitnessRemoteDataSource.sendFitnessSave(
            request,
            userToken,
        )
    }
}
