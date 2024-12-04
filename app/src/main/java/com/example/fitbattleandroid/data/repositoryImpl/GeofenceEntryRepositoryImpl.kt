package com.example.fitbattleandroid.data.repositoryImpl

import com.example.fitbattleandroid.data.api.dto.geofence.EntryGeoFenceReq
import com.example.fitbattleandroid.data.api.dto.geofence.EntryGeoFenceRes
import com.example.fitbattleandroid.data.remote.EncounterRemoteDatasource
import com.example.fitbattleandroid.data.repository.GeofenceEntryRepository

class GeofenceEntryRepositoryImpl(
    private val remoteDatasource: EncounterRemoteDatasource,
) : GeofenceEntryRepository {
    override suspend fun sendGeofenceEntryRequest(
        entryGeofenceReq: EntryGeoFenceReq,
        userToken: String,
    ): EntryGeoFenceRes = remoteDatasource.sendGeoFenceEntryRequest(entryGeofenceReq, userToken)
}
