package com.example.fitbattleandroid.data.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginRes(
    val userId: Int = -1,
    val name: String = "",
    val token: String = "",
)
