package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tenshin.app.data.model.Inventory
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

class InventoryViewModel(
    private val repository: WarframeRepository = WarframeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Idle)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _isHacked = MutableStateFlow(false)
    val isHacked: StateFlow<Boolean> = _isHacked.asStateFlow()

    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun syncInventory() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState.Syncing
            repository.syncInventory()
                .onSuccess { inventory ->
                    _uiState.value = InventoryUiState.Success(inventory)
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
                            
                            // Si acabamos de recibir el inventario y estábamos en modo hacked, volver a la normalidad tras un breve delay
                            if (_isHacked.value) {
                                viewModelScope.launch {
                                    delay(3000) // Mantener el efecto 3 segundos después de la sync
                                    _isHacked.value = false
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Log error
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _isHacked.value = false
            }
        })
    }

    fun uploadInventory(inventory: Inventory) {
        viewModelScope.launch {
            repository.backupInventory(inventory)
                .onFailure { error ->
                    _uiState.value = InventoryUiState.Error(error.message ?: "Upload failed")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.cancel()
    }
}
