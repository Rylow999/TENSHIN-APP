package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.remote.MarketOrder
import com.tenshin.app.data.repository.WarframeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MarketUiState {
    object Idle : MarketUiState
    object Loading : MarketUiState
    data class Success(val orders: List<MarketOrder>) : MarketUiState
    data class Error(val message: String) : MarketUiState
}

class MarketViewModel : ViewModel() {
    private val repository = WarframeRepository()
    private val _uiState = MutableStateFlow<MarketUiState>(MarketUiState.Idle)
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    fun fetchOrders(itemName: String) {
        val urlName = itemName.lowercase().replace(" ", "_")
        viewModelScope.launch {
            _uiState.value = MarketUiState.Loading
            repository.getMarketOrders(urlName)
                .onSuccess { orders ->
                    _uiState.value = MarketUiState.Success(orders.filter { it.user.status == "ingame" })
                }
                .onFailure { error ->
                    _uiState.value = MarketUiState.Error("No se encontraron órdenes activas para este ítem.")
                }
        }
    }
}
