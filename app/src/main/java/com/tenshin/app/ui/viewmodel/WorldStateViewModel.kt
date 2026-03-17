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

sealed interface WorldUiState {
    object Loading : WorldUiState
    data class Success(
        val worldState: WorldStateResponse,
        val invasions: List<Invasion>,
        val fissures: List<Fissure>
    ) : WorldUiState
    data class Error(val message: String) : WorldUiState
}

class WorldStateViewModel : ViewModel() {
    private val repository = WarframeRepository()
    private val _uiState = MutableStateFlow<WorldUiState>(WorldUiState.Loading)
    val uiState: StateFlow<WorldUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
        startAutoRefresh()
    }

    fun refreshAll() {
        viewModelScope.launch {
            _uiState.value = WorldUiState.Loading
            try {
                val worldStateDeferred = async { repository.getWorldState() }
                val invasionsDeferred = async { repository.getInvasions() }
                val fissuresDeferred = async { repository.getFissures() }

                val worldState = worldStateDeferred.await().getOrThrow()
                val invasions = invasionsDeferred.await().getOrDefault(emptyList())
                val fissures = fissuresDeferred.await().getOrDefault(emptyList())

                _uiState.value = WorldUiState.Success(worldState, invasions, fissures)
            } catch (e: Exception) {
                _uiState.value = WorldUiState.Error(e.message ?: "Error al sincronizar datos del Sistema Origen")
            }
        }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(120000) // 2 minutos
                refreshAll()
            }
        }
    }
}
