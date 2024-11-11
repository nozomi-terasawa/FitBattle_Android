package com.example.fitbattleandroid.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object DataStoreManager {
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    private val SHARE_HEALTH_DATA_KEY = booleanPreferencesKey("share_health_data")

    suspend fun saveAuthToken(
        context: Context,
        token: String,
    ) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    fun getAuthToken(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY] ?: ""
        }

    suspend fun saveShareHealthDataAllowed(
        context: Context,
        shareHealthData: Boolean,
    ) {
        context.dataStore.edit { preferences ->
            preferences[SHARE_HEALTH_DATA_KEY] = shareHealthData
        }
    }

    fun getShareHealthDataAllowed(context: Context): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[SHARE_HEALTH_DATA_KEY] ?: false
        }
}
