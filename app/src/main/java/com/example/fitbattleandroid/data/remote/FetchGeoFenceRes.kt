package com.example.fitbattleandroid.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FetchGeoFenceRes(
    val success: Boolean,
    val geoFence: List<GeoFenceInfoRes>,
)
