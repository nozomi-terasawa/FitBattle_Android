package com.example.fitbattleandroid.data.repository

import com.example.fitbattleandroid.data.api.dto.geofence.FetchGeoFenceRes

interface FetchGeoFenceInfoRepository {
    suspend fun fetchGeoFenceInfo(userToken: String): FetchGeoFenceRes
}
