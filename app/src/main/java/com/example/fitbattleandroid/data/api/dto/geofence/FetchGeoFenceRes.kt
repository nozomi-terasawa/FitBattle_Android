package com.example.fitbattleandroid.data.api.dto.geofence

import kotlinx.serialization.Serializable

@Serializable
data class FetchGeoFenceRes(
    val success: Boolean,
    val geoFence: List<GeoFenceInfoRes>,
)
