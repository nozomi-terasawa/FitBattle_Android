package com.example.fitbattleandroid.data.repository

import com.example.fitbattleandroid.data.api.dto.fitness.SaveFitnessReq

interface SaveFitnessRepository {
    suspend fun saveFitnessData(request: SaveFitnessReq)
}
