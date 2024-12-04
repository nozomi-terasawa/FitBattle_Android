package com.example.fitbattleandroid.data.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRes(
    val userId: Int,
    val token: String,
)
