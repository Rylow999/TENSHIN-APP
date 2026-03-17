package com.tenshin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.delay

@Composable
fun SyncScreen(
    viewModel: InventoryViewModel,
    onSyncComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentStep by remember { mutableStateOf(1) }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is InventoryUiState.Syncing -> {
                if (currentStep == 1) {
                    delay(800)
                    currentStep = 2
                }
            }
            is InventoryUiState.Success -> {
                currentStep = 3
                delay(1200)
                onSyncComplete()
            }
            is InventoryUiState.Error -> {
                currentStep = 1 
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "sync_icon")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(ColorAccent.copy(alpha = 0.1f))
                .border(2.dp, ColorAccent.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = ColorAccent,
                modifier = Modifier
                    .size(60.dp)
                    .graphicsLayer { rotationZ = if (uiState is InventoryUiState.Syncing) rotation else 0f }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "ESTABLECIENDO VÍNCULO",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAccent,
            letterSpacing = 4.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sincronizando arsenal con la red neuronal Tenshin",
            fontSize = 12.sp,
            color = ColorTextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
            SyncStepItem(
                number = 1,
                label = "Buscando PC en red local...",
                isActive = currentStep == 1,
                isDone = currentStep > 1
            )
            SyncStepItem(
                number = 2,
                label = "Descargando inventario...",
                isActive = currentStep == 2,
                isDone = currentStep > 2
            )
            SyncStepItem(
                number = 3,
                label = "Vínculo completado",
                isActive = currentStep == 3,
                isDone = currentStep >= 3
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        if (uiState !is InventoryUiState.Syncing && uiState !is InventoryUiState.Success) {
            Button(
                onClick = { viewModel.syncInventory() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text("INICIAR SINCRONIZACIÓN", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            
            TextButton(
                onClick = onSyncComplete,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("OMITIR POR AHORA", color = ColorTextMuted, fontSize = 12.sp)
            }
        }

        if (uiState is InventoryUiState.Error) {
            Surface(
                color = Color.Red.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Text(
                    text = (uiState as InventoryUiState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SyncStepItem(number: Int, label: String, isActive: Boolean, isDone: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .alpha(if (isActive || isDone) 1f else 0.3f)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isDone) ColorGreen else if (isActive) ColorAccent else Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(20.dp))
            } else {
                Text(number.toString(), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (isDone) ColorGreen else if (isActive) ColorText else ColorTextMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )

        if (isActive) {
            Spacer(Modifier.weight(1f))
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = ColorAccent)
        }
    }
}
