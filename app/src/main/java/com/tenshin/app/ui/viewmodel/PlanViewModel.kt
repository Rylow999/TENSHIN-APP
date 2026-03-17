package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PlanUiState {
    object Loading : PlanUiState
    data class Success(val plan: String) : PlanUiState
    data class Error(val message: String) : PlanUiState
}

class PlanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PlanUiState>(PlanUiState.Loading)
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    // En un entorno real, la API Key no debe estar en el código. 
    // Para el código abierto, se recomienda usar una variable de entorno o local.properties.
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_GEMINI_API_KEY_HERE" 
    )

    fun generateDailyPlan(inventoryData: String, marketTrends: String) {
        viewModelScope.launch {
            _uiState.value = PlanUiState.Loading
            try {
                val prompt = """
                    Actúa como Tenshin de Warframe. Analiza los siguientes datos:
                    Inventario: ${inventoryData}
                    Tendencias de Mercado: ${marketTrends}
                    
                    Genera un "Plan del Día" breve y épico que incluya:
                    1. Un ítem valioso para farmear hoy según el mercado.
                    2. Un objetivo de pesca o minería para conseguir insignias.
                    3. Un consejo táctico de mercado.
                    Usa un tono solemne y guerrero. Máximo 150 palabras.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                _uiState.value = PlanUiState.Success(response.text ?: "No se pudo obtener el plan.")
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error("El Vacío está inquieto: ${e.message}")
            }
        }
    }
}
