package com.tenshin.app.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface WarframeMarketApi {
    @GET("items/{item_url_name}/statistics")
    suspend fun getItemStatistics(@Path("item_url_name") itemUrlName: String): MarketStatisticsResponse

    @GET("items/{item_url_name}/orders")
    suspend fun getItemOrders(@Path("item_url_name") itemUrlName: String): MarketOrdersResponse
}

data class MarketStatisticsResponse(
    val payload: MarketStatisticsPayload
)

data class MarketStatisticsPayload(
    val statistics_closed: List<MarketStatistic>
)

data class MarketStatistic(
    val datetime: String,
    val volume: Int,
    val min_price: Double,
    val max_price: Double,
    val avg_price: Double,
    val wa_price: Double,
    val median: Double,
    val order_type: String
)

data class MarketOrdersResponse(
    val payload: MarketOrdersPayload
)

data class MarketOrdersPayload(
    val orders: List<MarketOrder>
)

data class MarketOrder(
    val id: String,
    @SerializedName("platinum") val platinum: Int,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("order_type") val orderType: String,
    @SerializedName("user") val user: MarketUser,
    @SerializedName("platform") val platform: String,
    @SerializedName("region") val region: String
)

data class MarketUser(
    val ingame_name: String,
    val status: String,
    val reputation: Int
)
