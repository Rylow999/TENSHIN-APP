package com.tenshin.app.data.remote

import retrofit2.http.GET

interface WarframeStatApi {
    @GET("pc/voidTrader")
    suspend fun getVoidTrader(): VoidTraderResponse

    @GET("pc")
    suspend fun getWorldState(): WorldStateResponse
}

data class VoidTraderResponse(
    val activation: String,
    val expiry: String,
    val character: String,
    val location: String,
    val inventory: List<VoidTraderItem>,
    val active: Boolean
)

data class VoidTraderItem(
    val item: String,
    val ducats: Int,
    val credits: Int
)

data class WorldStateResponse(
    val cetusCycle: CetusCycle,
    val vallisCycle: VallisCycle,
    val cambionCycle: CambionCycle
)

data class CetusCycle(val isDay: Boolean, val timeLeft: String)
data class VallisCycle(val isWarm: Boolean, val timeLeft: String)
data class CambionCycle(val active: String, val timeLeft: String)
