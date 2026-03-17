package com.tenshin.app.data.remote

import com.tenshin.app.data.model.Inventory
import okhttp3.MultipartBody
import retrofit2.http.*

interface WarframeHelperApi {
    @GET("inventory")
    suspend fun downloadInventory(): Inventory

    @POST("inventory")
    suspend fun uploadInventory(@Body inventory: Inventory): SimpleResponse

    @Multipart
    @POST("inventory/file")
    suspend fun uploadInventoryFile(@Part file: MultipartBody.Part): SimpleResponse
}

data class SimpleResponse(
    val success: Boolean,
    val message: String
)
