package com.tenshin.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tenshin.app.data.model.InventoryItem
import com.tenshin.app.data.remote.Auction
import com.tenshin.app.data.repository.WarframeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RivenUiState {
    object Loading : RivenUiState
    data class Success(val rivens: List<RivenAnalysis>) : RivenUiState
    data class Error(val message: String) : RivenUiState
}

data class RivenAnalysis(
    val item: InventoryItem,
    val estimatedValue: Int?,
    val verdict: String, // "GOD-TIER", "SOLID", "ROLL", "TRASH"
    val comparableAuctions: List<Auction> = emptyList()
)

class RivenViewModel(
    private val repository: WarframeRepository = WarframeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<RivenUiState>(RivenUiState.Loading)
    val uiState: StateFlow<RivenUiState> = _uiState.asStateFlow()

    fun analyzeRivens(inventoryItems: List<InventoryItem>) {
        viewModelScope.launch {
            _uiState.value = RivenUiState.Loading
            try {
                val rivens = inventoryItems.filter { it.isRiven }
                val analysisList = rivens.map { riven ->
                    val weaponUrl = riven.weaponName?.lowercase()?.replace(" ", "_")?.replace("'", "") ?: ""
                    val auctionsResult = if (weaponUrl.isNotEmpty()) {
                        repository.searchAuctions(weaponUrl)
                    } else Result.success(emptyList())

                    val auctions = auctionsResult.getOrDefault(emptyList())
                    val prices = auctions.mapNotNull { it.buyoutPrice }.filter { it > 0 }
                    val avgPrice = if (prices.isNotEmpty()) prices.average().toInt() else null
                    
                    RivenAnalysis(
                        item = riven,
                        estimatedValue = avgPrice,
                        verdict = calculateVerdict(riven, avgPrice),
                        comparableAuctions = auctions.take(3)
                    )
                }
                _uiState.value = RivenUiState.Success(analysisList)
            } catch (e: Exception) {
                _uiState.value = RivenUiState.Error(e.message ?: "Error al analizar Rivens")
            }
        }
    }

    private fun calculateVerdict(riven: InventoryItem, avgPrice: Int?): String {
        val stats = riven.rivenStats ?: return "ROLL"
        
        // Stats élite: Multishot, Crit Chance, Crit Damage, Damage, Elemental (si es el correcto)
        val eliteStats = listOf("multishot", "critical_chance", "critical_damage", "damage")
        val eliteCount = stats.count { stat -> 
            eliteStats.any { elite -> stat.name.lowercase().contains(elite) } && stat.positive 
        }
        
        val hasNegative = stats.any { !it.positive }

        return when {
            eliteCount >= 3 && hasNegative -> "GOD-TIER"
            eliteCount >= 2 -> "SOLID"
            (avgPrice ?: 0) > 800 -> "VALUABLE"
            else -> "ROLL"
        }
    }
}
