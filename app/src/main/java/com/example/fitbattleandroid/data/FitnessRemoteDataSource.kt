package com.example.fitbattleandroid.data

import com.example.fitbattleandroid.data.remote.SaveFitnessReq
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
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
    suspend fun sendFitnessSave(request: SaveFitnessReq) {
        client.post("http://192.168.224.234:7070/api/v1/fitness/save") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
