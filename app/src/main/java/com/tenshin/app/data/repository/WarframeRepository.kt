package com.tenshin.app.data.repository

import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.*
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WarframeRepository {
    private val marketApi = NetworkModule.warframeMarketApi
    private val statApi = NetworkModule.warframeStatApi
    
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

    suspend fun getBaroData(): Result<VoidTraderResponse> = withContext(Dispatchers.IO) {
        try {
            val response = statApi.getVoidTrader()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWorldState(): Result<WorldStateResponse> = withContext(Dispatchers.IO) {
        try {
            Result.success(statApi.getWorldState())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInvasions(): Result<List<Invasion>> = withContext(Dispatchers.IO) {
        try {
            Result.success(statApi.getInvasions())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFissures(): Result<List<Fissure>> = withContext(Dispatchers.IO) {
        try {
            Result.success(statApi.getFissures())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMarketOrders(itemUrlName: String): Result<List<MarketOrder>> = withContext(Dispatchers.IO) {
        try {
            val response = marketApi.getItemOrders(itemUrlName)
            Result.success(response.payload.orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
