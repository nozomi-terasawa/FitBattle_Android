package com.example.fitbattleandroid.data.api.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginReq(
    val email: String,
    val password: String,
)
