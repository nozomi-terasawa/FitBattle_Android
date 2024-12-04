package com.example.fitbattleandroid.data.repository

import com.example.fitbattleandroid.data.api.dto.geofence.EntryGeoFenceReq
import com.example.fitbattleandroid.data.api.dto.geofence.EntryGeoFenceRes

interface GeofenceEntryRepository {
    suspend fun sendGeofenceEntryRequest(
        entryGeofenceReq: EntryGeoFenceReq,
        userToken: String,
    ): EntryGeoFenceRes
}
