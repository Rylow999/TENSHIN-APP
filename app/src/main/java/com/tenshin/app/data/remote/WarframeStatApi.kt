package com.tenshin.app.data.remote

import retrofit2.http.GET

interface WarframeStatApi {
    @GET("pc/voidTrader")
    suspend fun getVoidTrader(): VoidTraderResponse

    @GET("pc")
    suspend fun getWorldState(): WorldStateResponse

    @GET("pc/invasions")
    suspend fun getInvasions(): List<Invasion>

    @GET("pc/fissures")
    suspend fun getFissures(): List<Fissure>
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
    val cambionCycle: CambionCycle,
    val zarimanCycle: ZarimanCycle
)

data class CetusCycle(val isDay: Boolean, val timeLeft: String, val shortString: String)
data class VallisCycle(val isWarm: Boolean, val timeLeft: String, val shortString: String)
data class CambionCycle(val active: String, val timeLeft: String)
data class ZarimanCycle(val state: String, val timeLeft: String)

data class Invasion(
    val id: String,
    val node: String,
    val desc: String,
    val attackerReward: Reward?,
    val defenderReward: Reward?,
    val completion: Float,
    val completed: Boolean
)

data class Reward(val itemString: String, val thumbnail: String?)

data class Fissure(
    val id: String,
    val node: String,
    val missionType: String,
    val tier: String,
    val tierNum: Int,
    val enemy: String,
    val expiry: String,
    val expired: Boolean,
    val active: Boolean
)
