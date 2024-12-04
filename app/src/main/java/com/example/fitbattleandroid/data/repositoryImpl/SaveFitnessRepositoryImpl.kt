package com.example.fitbattleandroid.data.repositoryImpl

import com.example.fitbattleandroid.data.api.dto.fitness.SaveFitnessReq
import com.example.fitbattleandroid.data.remote.FitnessRemoteDataSource

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
