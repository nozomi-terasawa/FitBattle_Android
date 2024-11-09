package com.example.fitbattleandroid.repository

import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.data.remote.EntryGeoFenceRes

interface GeofenceEntryRepository {
    suspend fun sendGeofenceEntryRequest(
        entryGeofenceReq: EntryGeoFenceReq,
        userToken: String,
    ): EntryGeoFenceRes
}
