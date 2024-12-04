package com.example.fitbattleandroid.data.repository

import com.example.fitbattleandroid.data.api.dto.auth.UserCreateReq
import com.example.fitbattleandroid.data.api.dto.auth.UserCreateRes
import com.example.fitbattleandroid.data.api.dto.auth.UserLoginReq
import com.example.fitbattleandroid.data.api.dto.auth.UserLoginRes

interface AuthRepository {
    suspend fun register(userCreateReq: UserCreateReq): UserCreateRes

    suspend fun login(userLoginReq: UserLoginReq): UserLoginRes
}
