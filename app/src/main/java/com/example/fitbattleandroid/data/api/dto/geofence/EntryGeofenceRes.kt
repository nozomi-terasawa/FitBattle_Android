package com.example.fitbattleandroid.data.api.dto.geofence

import kotlinx.serialization.Serializable

@Serializable
data class EntryGeoFenceRes(
    val success: Boolean,
    val geoFenceEntryLogId: Int,
    val token: String,
    val passingMember: List<MemberInfo>,
)

@Serializable
data class MemberInfo(
    val id: Int,
    val name: String,
    val iconUrl: String,
)
