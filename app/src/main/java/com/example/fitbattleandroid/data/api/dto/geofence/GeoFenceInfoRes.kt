package com.example.fitbattleandroid.data.api.dto.geofence

import kotlinx.serialization.Serializable

@Serializable
data class GeoFenceInfoRes(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
)
