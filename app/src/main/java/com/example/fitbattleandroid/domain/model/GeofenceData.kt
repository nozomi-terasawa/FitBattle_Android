package com.example.fitbattleandroid.domain.model

data class GeofenceData(
    val requestId: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
)
