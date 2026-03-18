package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.remote.Fissure
import com.tenshin.app.data.remote.Invasion
import com.tenshin.app.data.remote.WorldStateResponse
import com.tenshin.app.data.repository.WarframeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SistemaUiState {
    object Loading : SistemaUiState
    data class Success(
        val worldState: WorldStateResponse,
        val invasions: List<Invasion>,
        val fissures: List<Fissure>,
        val isSteelPath: Boolean = false
    ) : SistemaUiState
    data class Error(val message: String) : SistemaUiState
}

class SistemaViewModel : ViewModel() {
    private val repository = WarframeRepository()
    private val _uiState = MutableStateFlow<SistemaUiState>(SistemaUiState.Loading)
    val uiState: StateFlow<SistemaUiState> = _uiState.asStateFlow()

    private var _isSteelPath = false

    init {
        refreshAll()
        startAutoRefresh()
    }

    fun toggleSteelPath() {
        _isSteelPath = !_isSteelPath
        val current = _uiState.value
        if (current is SistemaUiState.Success) {
            _uiState.value = current.copy(isSteelPath = _isSteelPath)
        }
    }

    fun refreshAll() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is SistemaUiState.Success) {
                _uiState.value = SistemaUiState.Loading
            }
            
            try {
                val worldStateDeferred = async { repository.getWorldState() }
                val invasionsDeferred = async { repository.getInvasions() }
                val fissuresDeferred = async { repository.getFissures() }

                val ws = worldStateDeferred.await().getOrNull()
                val inv = invasionsDeferred.await().getOrDefault(emptyList())
                val fis = fissuresDeferred.await().getOrDefault(emptyList())

                if (ws != null) {
                    _uiState.value = SistemaUiState.Success(ws, inv, fis, _isSteelPath)
                } else {
                    _uiState.value = SistemaUiState.Error("Fallo al conectar con el Sistema Origen")
                }
            } catch (e: Exception) {
                _uiState.value = SistemaUiState.Error(e.localizedMessage ?: "Error de red")
            }
        }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(60000)
                refreshAll()
            }
        }
    }
}
