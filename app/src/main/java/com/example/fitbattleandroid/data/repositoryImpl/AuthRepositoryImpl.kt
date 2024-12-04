package com.example.fitbattleandroid.data.repositoryImpl

import com.example.fitbattleandroid.data.api.dto.auth.UserCreateReq
import com.example.fitbattleandroid.data.api.dto.auth.UserCreateRes
import com.example.fitbattleandroid.data.api.dto.auth.UserLoginReq
import com.example.fitbattleandroid.data.api.dto.auth.UserLoginRes
import com.example.fitbattleandroid.data.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthRepositoryImpl : AuthRepository {
    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }

    override suspend fun register(userCreateReq: UserCreateReq): UserCreateRes {
        val res =
            client.post("http://192.168.11.3:7070/api/v1/user/create") {
                contentType(ContentType.Application.Json)
                setBody(userCreateReq)
            }
        // レスポンスからボディを取得して変数に追加
        val responseBody = res.body<UserCreateRes>()
        return responseBody
    }

    override suspend fun login(userLoginReq: UserLoginReq): UserLoginRes {
        val res =
            client.post("http://192.168.11.3:7070/api/v1/user/login") {
                contentType(ContentType.Application.Json)
                setBody(userLoginReq)
            }
        val responseBody = res.body<UserLoginRes>()
        return responseBody
    }
}
