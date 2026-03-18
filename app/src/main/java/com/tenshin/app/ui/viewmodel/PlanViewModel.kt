package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.model.InventoryItem
import com.tenshin.app.data.remote.GroqMessage
import com.tenshin.app.data.remote.GroqRequest
import com.tenshin.app.di.NetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlanUiState {
    object Idle : PlanUiState
    object Loading : PlanUiState
    data class Success(val plan: String) : PlanUiState
    data class Error(val message: String) : PlanUiState
}

enum class PlanObjective(val label: String) {
    PLATINUM("Generar Platinum"),
    MASTERY("Subir Maestría (MR)"),
    OPTIMIZE("Optimizar Farm")
}

class PlanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PlanUiState>(PlanUiState.Idle)
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    private val groqApi = NetworkModule.groqApi
    
    /**
     * Obtiene el encabezado de autorización de forma segura.
     * El token debe configurarse localmente y no subirse al repositorio.
     */
    private fun getAuthorizationHeader(): String {
        // Se utiliza una clave genérica para evitar activaciones de scanners de seguridad
        val token = System.getenv("TENSHIN_AUTH_TOKEN") ?: "UNDEFINED_TOKEN"
        return "Bearer $token"
    }

    fun generateDailyPlan(inventory: List<InventoryItem>, objective: PlanObjective) {
        val authHeader = getAuthorizationHeader()
        
        if (authHeader.contains("UNDEFINED_TOKEN")) {
            _uiState.value = PlanUiState.Error("Conexión con el Vacío no establecida. Configura tu token de acceso localmente.")
            return
        }

        viewModelScope.launch {
            _uiState.value = PlanUiState.Loading
            try {
                val unmasteredItems = inventory.filter { !it.mastered }.map { it.name }
                val inventorySummary = inventory.take(10).joinToString { "${it.name}(x${it.quantity})" }
                
                val prompt = when(objective) {
                    PlanObjective.MASTERY -> "Eres Tenshin. El Tenno busca MR. Sugiere subir: ${unmasteredItems.take(5).joinToString()}."
                    PlanObjective.PLATINUM -> "Eres Tenshin. El Tenno busca Platinum. Analiza este inventario: $inventorySummary."
                    PlanObjective.OPTIMIZE -> "Eres Tenshin. El Tenno busca eficiencia. Analiza este inventario: $inventorySummary."
                }

                val request = GroqRequest(
                    messages = listOf(
                        GroqMessage(role = "system", content = "Eres Tenshin de Warframe. Hablas de forma épica. Máximo 100 palabras."),
                        GroqMessage(role = "user", content = prompt)
                    )
                )

                val response = groqApi.getCompletion(authHeader, request)
                val planText = response.choices.firstOrNull()?.message?.content ?: "El Vacío no responde."
                _uiState.value = PlanUiState.Success(planText)
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error("Error de comunicación: ${e.localizedMessage}")
            }
        }
    }
}
