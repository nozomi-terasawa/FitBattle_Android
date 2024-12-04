package com.example.fitbattleandroid.data.api.dto.geofence

import kotlinx.serialization.Serializable

@Serializable
data class EntryGeoFenceReq(
    val userId: Int,
    val geoFenceId: Int,
    val entryTime: String,
)
