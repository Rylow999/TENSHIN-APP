package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.model.Post
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ══════════════════════════════════════════
//  Estado de la UI
// ══════════════════════════════════════════
sealed interface UiState {
    object Loading : UiState
    data class Success(val data: List<Post>) : UiState
    data class Error(val message: String) : UiState
}

// ══════════════════════════════════════════
//  HomeViewModel
// ══════════════════════════════════════════
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _isHacked = MutableStateFlow(false)
    val isHacked: StateFlow<Boolean> = _isHacked.asStateFlow()

    init {
        fetchPosts()
    }

    fun syncInventory() {
        // Implementación de sincronización
    }

    fun setHacked(hacked: Boolean) {
        _isHacked.value = hacked
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // LLamada asíncrona a la API
                val response = NetworkModule.apiService.getPosts()
                _uiState.value = UiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
