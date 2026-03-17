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
        val fissures: List<Fissure>
    ) : SistemaUiState
    data class Error(val message: String) : SistemaUiState
}

class SistemaViewModel : ViewModel() {
    private val repository = WarframeRepository()
    private val _uiState = MutableStateFlow<SistemaUiState>(SistemaUiState.Loading)
    val uiState: StateFlow<SistemaUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
        startAutoRefresh()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.value = SistemaUiState.Loading
            try {
                // Ejecutamos las peticiones en paralelo para evitar bloqueos
                val worldStateDeferred = async { repository.getWorldState() }
                val invasionsDeferred = async { repository.getInvasions() }
                val fissuresDeferred = async { repository.getFissures() }

                val worldStateResult = worldStateDeferred.await()
                val invasionsResult = invasionsDeferred.await()
                val fissuresResult = fissuresDeferred.await()

                // Verificamos el resultado principal (WorldState)
                if (worldStateResult.isSuccess) {
                    _uiState.value = SistemaUiState.Success(
                        worldState = worldStateResult.getOrThrow(),
                        invasions = invasionsResult.getOrDefault(emptyList()),
                        fissures = fissuresResult.getOrDefault(emptyList())
                    )
                } else {
                    _uiState.value = SistemaUiState.Error("No se pudo obtener el estado del Sistema Origen")
                }
            } catch (e: Exception) {
                _uiState.value = SistemaUiState.Error(e.message ?: "Error de red al sincronizar el Sistema")
            }
        }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(180000) // 3 minutos para no saturar la API
                refreshAll()
            }
        }
    }
}
