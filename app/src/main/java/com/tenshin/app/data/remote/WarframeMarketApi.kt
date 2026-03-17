package com.tenshin.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface WarframeMarketApi {
    @GET("items/{item_url_name}/statistics")
    suspend fun getItemStatistics(@Path("item_url_name") itemUrlName: String): MarketStatisticsResponse
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
