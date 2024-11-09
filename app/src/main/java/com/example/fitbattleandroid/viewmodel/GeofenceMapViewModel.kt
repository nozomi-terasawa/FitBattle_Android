package com.example.fitbattleandroid.viewmodel

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.fitbattleandroid.MyApplication
import com.example.fitbattleandroid.data.remote.EntryGeoFenceReq
import com.example.fitbattleandroid.data.remote.EntryGeoFenceRes
import com.example.fitbattleandroid.repositoryImpl.GeofenceEntryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GeofenceMapViewModel(
    application: MyApplication,
    private val repository: GeofenceEntryRepositoryImpl,
) : AndroidViewModel(application) {
    private val _geofenceEntryState = MutableStateFlow<GeofenceEntryState>(GeofenceEntryState.Loading)
    val geofenceEntryState: StateFlow<GeofenceEntryState> = _geofenceEntryState.asStateFlow()

    private val userToken = (getApplication() as MyApplication).userToken

    // ジオフェンス入室リクエスト関数
    suspend fun sendGeoFenceEntryRequest(entry: EntryGeoFenceReq) {
        try {
            if (userToken == null) {
                _geofenceEntryState.value = GeofenceEntryState.Error
                return
            } else {
                val res = repository.sendGeofenceEntryRequest(entry, userToken)
                _geofenceEntryState.value = GeofenceEntryState.Success(res)
            }
        } catch (e: Exception) {
            Log.d("result", e.toString())
            _geofenceEntryState.value = GeofenceEntryState.Error
        }
    }
}

sealed interface GeofenceEntryState {
    data object Loading : GeofenceEntryState

    data class Success(
        val entryGeoFenceRes: EntryGeoFenceRes,
    ) : GeofenceEntryState

    data object Error : GeofenceEntryState
}
