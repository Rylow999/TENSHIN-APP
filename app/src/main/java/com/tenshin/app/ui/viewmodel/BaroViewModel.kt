package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.remote.VoidTraderResponse
import com.tenshin.app.data.repository.WarframeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BaroUiState {
    object Loading : BaroUiState
    data class Success(val baroData: VoidTraderResponse) : BaroUiState
    data class Error(val message: String) : BaroUiState
}

class BaroViewModel(
    private val repository: WarframeRepository = WarframeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<BaroUiState>(BaroUiState.Loading)
    val uiState: StateFlow<BaroUiState> = _uiState.asStateFlow()

    init {
        fetchBaroData()
    }

    fun fetchBaroData() {
        viewModelScope.launch {
            _uiState.value = BaroUiState.Loading
            repository.getBaroData()
                .onSuccess { data ->
                    _uiState.value = BaroUiState.Success(data)
                }
                .onFailure { error ->
                    _uiState.value = BaroUiState.Error(error.message ?: "Error al cargar datos de Baro")
                }
        }
    }
}
