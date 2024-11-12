package com.example.fitbattleandroid.repositoryImpl

import android.util.Log
import com.example.fitbattleandroid.repository.WebSocketRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.close

class WebSocketRepositoryImpl(
    private val client: HttpClient,
) : WebSocketRepository {
    private var session: DefaultClientWebSocketSession? = null

    override suspend fun connect(block: suspend DefaultClientWebSocketSession.() -> Unit) {
        try {
            client.webSocket(
                method = HttpMethod.Get,
                host = "192.168.11.3",
                port = 7070,
                path = "/api/v1/geofence/ws",
                request = {
                    url.parameters.append("roomID", "2")
                    url.parameters.append("userId", "3")
                },
            ) {
                session = this
                block()
            }
        } catch (e: Exception) {
            Log.e("WebSocket", e.toString())
        }
    }

    override suspend fun disconnect() {
        session?.close()
        session = null
    }
}
