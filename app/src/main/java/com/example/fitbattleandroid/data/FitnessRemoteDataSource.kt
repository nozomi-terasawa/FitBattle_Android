package com.example.fitbattleandroid.data

import com.example.fitbattleandroid.data.remote.SaveFitnessReq
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FitnessRemoteDataSource {
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

    // フィットネスデータの保存リクエスト
    suspend fun sendFitnessSave(
        request: SaveFitnessReq,
        userToken: String,
    ) {
        client.post("http://192.168.11.3:7070/api/v1/fitness/save") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $userToken")
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
