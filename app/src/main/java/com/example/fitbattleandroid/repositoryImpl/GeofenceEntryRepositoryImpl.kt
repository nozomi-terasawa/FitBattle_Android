package com.example.fitbattleandroid.repositoryImpl

import com.example.fitbattleandroid.data.EncounterRemoteDatasource
import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.data.remote.EntryGeoFenceRes
import com.example.fitbattleandroid.repository.GeofenceEntryRepository

class GeofenceEntryRepositoryImpl(
    private val remoteDatasource: EncounterRemoteDatasource,
) : GeofenceEntryRepository {
    override suspend fun sendGeofenceEntryRequest(
        entryGeofenceReq: EntryGeoFenceReq,
        userToken: String,
    ): EntryGeoFenceRes = remoteDatasource.sendGeoFenceEntryRequest(entryGeofenceReq, userToken)
}
