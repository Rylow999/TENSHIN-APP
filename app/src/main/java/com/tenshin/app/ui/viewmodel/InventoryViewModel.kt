package com.tenshin.app.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.NetworkDiscovery
import com.tenshin.app.data.repository.WarframeRepository
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

sealed interface InventoryUiState {
    object Idle : InventoryUiState
    object Syncing : InventoryUiState
    data class Success(val inventory: Inventory, val isRealTime: Boolean = false) : InventoryUiState
    data class Error(val message: String) : InventoryUiState
}

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WarframeRepository()
    private val discovery = NetworkDiscovery(application)
    private val sharedPrefs = application.getSharedPreferences("tenshin_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Idle)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _isHacked = MutableStateFlow(false)
    val isHacked: StateFlow<Boolean> = _isHacked.asStateFlow()

    private var webSocket: WebSocket? = null

    init {
        loadCachedInventory()
    }

    private fun loadCachedInventory() {
        val cachedJson = sharedPrefs.getString("cached_inventory", null)
        if (cachedJson != null) {
            try {
                val inventory = gson.fromJson(cachedJson, Inventory::class.java)
                _uiState.value = InventoryUiState.Success(inventory)
            } catch (e: Exception) { }
        }
    }

    private fun saveInventoryToCache(inventory: Inventory) {
        val json = gson.toJson(inventory)
        sharedPrefs.edit().putString("cached_inventory", json).apply()
    }

    fun syncInventory() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Syncing
            
            if (NetworkModule.getHelperIp() == null) {
                val discoveredIp = discovery.discoverHelperIp()
                if (discoveredIp != null) {
                    NetworkModule.setHelperIp(discoveredIp)
                } else {
                    _uiState.value = InventoryUiState.Error("No se encontró la PC en la red local.")
                    return@launch
                }
            }

            repository.syncInventory()
                .onSuccess { inventory ->
                    _uiState.value = InventoryUiState.Success(inventory)
                    saveInventoryToCache(inventory)
                    startRealTimeSync()
                }
                .onFailure { error ->
                    _uiState.value = InventoryUiState.Error(error.message ?: "Sync failed")
                }
        }
    }

    private fun startRealTimeSync() {
        webSocket?.cancel()
        webSocket = NetworkModule.createWebSocket(object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = gson.fromJson(text, JsonObject::class.java)
                    val type = json.get("type")?.asString
                    
                    when (type) {
                        "system_status" -> {
                            if (json.get("status")?.asString == "HACKED_1999") {
                                _isHacked.value = true
                            }
                        }
                        "inventory", "inventory_update" -> {
                            val payload = json.get("payload")
                            val inventory = gson.fromJson(payload, Inventory::class.java)
                            _uiState.value = InventoryUiState.Success(inventory, isRealTime = true)
                            saveInventoryToCache(inventory)
                            
                            if (_isHacked.value) {
                                viewModelScope.launch {
                                    delay(3000)
                                    _isHacked.value = false
                                }
                            }
                        }
                    }
                } catch (e: Exception) { }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isHacked.value = false
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.cancel()
    }
}
