package com.example.fitbattleandroid.data.repositoryImpl

import com.example.fitbattleandroid.data.api.dto.geofence.FetchGeoFenceRes
import com.example.fitbattleandroid.data.repository.FetchGeoFenceInfoRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FetchGeoFenceInfoRepositoryImpl
    @Inject
    constructor() : FetchGeoFenceInfoRepository {
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

        override suspend fun fetchGeoFenceInfo(userToken: String): FetchGeoFenceRes {
            try {
                val res =
                    client.get("http://192.168.11.3:7070/api/v1/location/geofence") {
                        headers {
                            append(HttpHeaders.Authorization, "Bearer $userToken")
                        }
                        contentType(ContentType.Application.Json)
                    }
                val responseBody = res.body<FetchGeoFenceRes>()
                return responseBody
            } catch (e: Exception) {
                return FetchGeoFenceRes(success = false, geoFence = emptyList())
            }
        }
    }
