package com.example.fitbattleandroid.repository

import com.example.fitbattleandroid.data.remote.FetchGeoFenceRes

interface FetchGeoFenceInfoRepository {
    suspend fun fetchGeoFenceInfo(userToken: String): FetchGeoFenceRes
}
