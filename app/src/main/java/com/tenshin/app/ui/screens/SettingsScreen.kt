package com.tenshin.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val ignoreTrash by viewModel.ignoreTrash.collectAsState()
    val showSteelPath by viewModel.showSteelPath.collectAsState()
    val enableNotifications by viewModel.enableNotifications.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CONFIGURACIÓN",
            style = MaterialTheme.typography.headlineSmall,
            color = ColorAccent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                SettingSwitch(
                    title = "Ignorar Basura",
                    subtitle = "No incluir ítems de bajo valor en el cálculo total.",
                    icon = Icons.Default.Delete,
                    checked = ignoreTrash,
                    onCheckedChange = { viewModel.setIgnoreTrash(it) }
                )
            }
            item {
                SettingSwitch(
                    title = "Modo Steel Path",
                    subtitle = "Mostrar alertas y datos del Camino de Acero.",
                    icon = Icons.Default.Security,
                    checked = showSteelPath,
                    onCheckedChange = { viewModel.setShowSteelPath(it) }
                )
            }
            item {
                SettingSwitch(
                    title = "Notificaciones Push",
                    subtitle = "Avisar sobre llegada de Baro y cambios en Wishlist.",
                    icon = Icons.Default.Notifications,
                    checked = enableNotifications,
                    onCheckedChange = { viewModel.setEnableNotifications(it) }
                )
            }
        }
    }
}

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorText)
                Text(subtitle, fontSize = 11.sp, color = ColorTextMuted)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ColorAccent,
                    checkedTrackColor = ColorAccent.copy(alpha = 0.5f)
                )
            )
        }
    }
}
