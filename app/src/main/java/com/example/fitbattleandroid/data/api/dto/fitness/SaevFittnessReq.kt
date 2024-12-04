package com.example.fitbattleandroid.data.api.dto.fitness

import kotlinx.serialization.Serializable

@Serializable
data class SaveFitnessReq(
    val userId: Int,
    val calories: Float,
    val timestamp: String,
)
