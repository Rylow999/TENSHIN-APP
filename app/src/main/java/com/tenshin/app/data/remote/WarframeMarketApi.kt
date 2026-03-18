package com.tenshin.app.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WarframeMarketApi {
    @GET("items/{item_url_name}/statistics")
    suspend fun getItemStatistics(@Path("item_url_name") itemUrlName: String): MarketStatisticsResponse

    @GET("items/{item_url_name}/orders")
    suspend fun getItemOrders(@Path("item_url_name") itemUrlName: String): MarketOrdersResponse

    @GET("riven/items")
    suspend fun getRivenItems(): RivenItemsResponse

    @GET("auctions/search")
    suspend fun searchAuctions(
        @Query("type") type: String = "riven",
        @Query("weapon_url_name") weaponUrlName: String,
        @Query("positive_stats") positiveStats: String? = null,
        @Query("negative_stats") negativeStats: String? = null,
        @Query("polarity") polarity: String? = null,
        @Query("mastery_rank") mr: Int? = null,
        @Query("sort_by") sort: String = "price_asc"
    ): AuctionResponse
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

data class RivenItemsResponse(
    val payload: RivenItemsPayload
)

data class RivenItemsPayload(
    val items: List<RivenItem>
)

data class RivenItem(
    val id: String,
    @SerializedName("item_name") val itemName: String,
    @SerializedName("url_name") val urlName: String,
    @SerializedName("group") val group: String,
    @SerializedName("icon") val icon: String
)

data class AuctionResponse(
    val payload: AuctionPayload
)

data class AuctionPayload(
    val auctions: List<Auction>
)

data class Auction(
    val id: String,
    @SerializedName("buyout_price") val buyoutPrice: Int?,
    @SerializedName("starting_price") val startingPrice: Int?,
    @SerializedName("top_bid") val topBid: Int?,
    @SerializedName("item") val item: AuctionItem,
    @SerializedName("owner") val owner: MarketUser,
    @SerializedName("is_direct_sell") val isDirectSell: Boolean
)

data class AuctionItem(
    val type: String,
    @SerializedName("weapon_url_name") val weaponUrlName: String,
    @SerializedName("re-rolls") val rolls: Int,
    val attributes: List<RivenAttribute>
)

data class RivenAttribute(
    val url_name: String,
    val value: Float,
    val positive: Boolean
)
