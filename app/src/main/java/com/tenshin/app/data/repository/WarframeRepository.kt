package com.tenshin.app.data.repository

import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.WarframeHelperApi
import com.tenshin.app.data.remote.WarframeMarketApi
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WarframeRepository {
    private val marketApi = NetworkModule.warframeMarketApi
    
    suspend fun syncInventory(): Result<Inventory> = withContext(Dispatchers.IO) {
        try {
            val helperApi = NetworkModule.getWarframeHelperApi() 
                ?: return@withContext Result.failure(Exception("PC no encontrada o IP no configurada"))
            
            val inventory = helperApi.downloadInventory()
            Result.success(inventory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun backupInventory(inventory: Inventory): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val helperApi = NetworkModule.getWarframeHelperApi()
                ?: return@withContext Result.failure(Exception("PC no encontrada"))

            val response = helperApi.uploadInventory(inventory)
            if (response.success) Result.success(true) 
            else Result.failure(Exception(response.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
