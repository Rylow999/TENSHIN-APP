package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.data.remote.MarketOrder
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.MarketUiState
import com.tenshin.app.ui.viewmodel.MarketViewModel

@Composable
fun PrecioScreen(viewModel: MarketViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "TENDENCIA DE MERCADO",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Ej: Volt Prime Chassis", color = ColorTextMuted) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ColorAccent) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    TextButton(onClick = { viewModel.fetchOrders(searchQuery) }) {
                        Text("BUSCAR", color = ColorAccent, fontWeight = FontWeight.Bold)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = ColorBorder,
                cursorColor = ColorAccent,
                focusedTextColor = ColorText,
                unfocusedTextColor = ColorText
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        when (val state = uiState) {
            is MarketUiState.Idle -> {
                MarketIdleView()
            }
            is MarketUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ColorAccent)
                }
            }
            is MarketUiState.Success -> {
                MarketOrderList(state.orders)
            }
            is MarketUiState.Error -> {
                ErrorBox(state.message)
            }
        }
    }
}

@Composable
fun MarketIdleView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "MERCADO EN VIVO",
                color = ColorAccent.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                "Consulta órdenes activas de jugadores ingame\ny encuentra el mejor precio ahora.",
                color = ColorTextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun MarketOrderList(orders: List<MarketOrder>) {
    val sellOrders = orders.filter { it.orderType == "sell" }.sortedBy { it.platinum }
    val buyOrders = orders.filter { it.orderType == "buy" }.sortedByDescending { it.platinum }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (sellOrders.isNotEmpty()) {
            item { SectionHeader("VENDEDORES (MÁS BARATOS)") }
            items(sellOrders.take(5)) { order ->
                OrderCard(order, ColorGreen)
            }
        }
        
        if (buyOrders.isNotEmpty()) {
            item { SectionHeader("COMPRADORES (MEJOR PRECIO)") }
            items(buyOrders.take(5)) { order ->
                OrderCard(order, ColorAccent)
            }
        }
    }
}

@Composable
fun OrderCard(order: MarketOrder, priceColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(order.user.ingame_name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ColorText)
                Text("Rep: ${order.user.reputation}", fontSize = 10.sp, color = ColorTextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${order.platinum}p", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = priceColor)
                Text("Cant: ${order.quantity}", fontSize = 10.sp, color = ColorTextMuted)
            }
        }
    }
}
