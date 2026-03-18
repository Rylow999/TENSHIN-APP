package com.tenshin.app.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tenshin.app.data.model.Inventory
import com.tenshin.app.data.remote.AutoConfig
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface InventoryUiState {
    object Idle : InventoryUiState
    data class Syncing(val step: Int) : InventoryUiState
    data class Success(val inventory: Inventory, val isRealTime: Boolean = false, val lastSyncDate: String = "") : InventoryUiState
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
        val lastDate = sharedPrefs.getString("last_sync_date", "Nunca") ?: "Nunca"
        if (cachedJson != null) {
            try {
                val inventory = gson.fromJson(cachedJson, Inventory::class.java)
                _uiState.value = InventoryUiState.Success(inventory, lastSyncDate = lastDate)
            } catch (e: Exception) { }
        }
    }

    fun setHelperConfig(ip: String, httpPort: Int, wsPort: Int) {
        AutoConfig.saveIp(getApplication(), ip)
        NetworkModule.setHelperConfig(ip, httpPort, wsPort)
        syncInventory()
    }

    fun syncInventory() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Syncing(1)
            
            if (NetworkModule.getHelperIp() == null) {
                val discoveredIp = discovery.discoverHelperIp()
                if (discoveredIp != null) {
                    NetworkModule.setHelperConfig(discoveredIp)
                    AutoConfig.saveIp(getApplication(), discoveredIp)
                } else {
                    _uiState.value = InventoryUiState.Error("PC no encontrada. Verifica el Helper o ingresa la IP.")
                    return@launch
                }
            }

            _uiState.value = InventoryUiState.Syncing(2)
            repository.syncInventory()
                .onSuccess { inventory ->
                    val now = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
                    _uiState.value = InventoryUiState.Success(inventory, lastSyncDate = now)
                    
                    val json = gson.toJson(inventory)
                    sharedPrefs.edit()
                        .putString("cached_inventory", json)
                        .putString("last_sync_date", now)
                        .apply()
                    
                    startRealTimeSync()
                }
                .onFailure { error ->
                    _uiState.value = InventoryUiState.Error("Fallo de red: ${error.localizedMessage}")
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
                            val now = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date())
                            _uiState.value = InventoryUiState.Success(inventory, isRealTime = true, lastSyncDate = now)
                            
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
