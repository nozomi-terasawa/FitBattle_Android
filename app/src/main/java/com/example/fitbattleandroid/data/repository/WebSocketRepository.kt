package com.example.fitbattleandroid.data.repository

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession

interface WebSocketRepository {
    suspend fun connect(block: suspend DefaultClientWebSocketSession.() -> Unit)

    suspend fun disconnect()
}
