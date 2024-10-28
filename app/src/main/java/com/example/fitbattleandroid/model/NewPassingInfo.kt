package com.example.fitbattleandroid.model

import kotlinx.serialization.Serializable

@Serializable
data class NewPassingInfo(
    val userId: Int,
    val geoFenceId: Int,
    val message: String,
    val passedUserId: Int,
    val passedUserName: String,
    val timestamp: String,
)
