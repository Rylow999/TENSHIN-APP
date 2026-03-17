package com.tenshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.ui.theme.ColorAccent
import com.tenshin.app.ui.theme.ColorGold
import com.tenshin.app.ui.viewmodel.PlanUiState
import com.tenshin.app.ui.viewmodel.PlanViewModel

@Composable
fun PlanScreen(
    viewModel: PlanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Simulamos carga de datos para el prompt
    LaunchedEffect(Unit) {
        viewModel.generateDailyPlan(
            inventoryData = "Excalibur Prime, 500 Platinum, Orokin Cells x10",
            marketTrends = "Glaive Prime en alza, Arcanos estables"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "DIRECTIVA DE TENSHIN",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = uiState) {
            is PlanUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorAccent)
                }
            }
            is PlanUiState.Success -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = ColorGold)
                            Spacer(Modifier.width(8.dp))
                            Text("PLAN DEL DÍA", fontWeight = FontWeight.Bold, color = ColorGold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = state.plan,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            is PlanUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = { viewModel.generateDailyPlan("...", "...") }) {
                    Text("Reintentar")
                }
            }
        }
    }
}
