package com.tenshin.app.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tenshin.app.ui.components.QrScanner
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.viewmodel.InventoryUiState
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun SyncScreen(
    viewModel: InventoryViewModel,
    onSyncComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var manualIp by remember { mutableStateOf("") }
    var showScanner by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showScanner = true
        else Toast.makeText(context, "Permiso de cámara necesario para QR", Toast.LENGTH_SHORT).show()
    }
    
    val currentStep = when (uiState) {
        is InventoryUiState.Syncing -> (uiState as InventoryUiState.Syncing).step
        is InventoryUiState.Success -> 3
        else -> 1
    }

    LaunchedEffect(uiState) {
        if (uiState is InventoryUiState.Success) {
            delay(1200) 
            onSyncComplete()
        }
    }

    if (showScanner) {
        Box(Modifier.fillMaxSize()) {
            QrScanner(onScan = { data ->
                try {
                    val uri = Uri.parse(data)
                    val ip = uri.getQueryParameter("ip")
                    val ws = uri.getQueryParameter("ws")?.toIntOrNull() ?: 8081
                    val http = uri.getQueryParameter("http")?.toIntOrNull() ?: 8080
                    
                    if (ip != null && ip.matches(Regex("""\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}"""))) {
                        viewModel.setHelperConfig(ip, http, ws)
                        showScanner = false
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "QR no reconocido", Toast.LENGTH_SHORT).show()
                }
            })
            IconButton(
                onClick = { showScanner = false },
                modifier = Modifier.padding(16.dp).align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "glow")
        val glowAlpha by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
            label = "glow_alpha"
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ColorAccent.copy(alpha = 0.1f))
                .border(2.dp, ColorAccent.copy(alpha = glowAlpha), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = ColorAccent,
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer { 
                        if (uiState is InventoryUiState.Syncing) {
                            rotationZ += 5f 
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "VÍNCULO TENSHIN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAccent,
            letterSpacing = 4.sp,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(40.dp))

        Column(modifier = Modifier.fillMaxWidth(0.85f)) {
            SyncStepItem(1, "Localizando PC...", currentStep == 1 && uiState is InventoryUiState.Syncing, currentStep > 1)
            SyncStepItem(2, "Descargando arsenal...", currentStep == 2 && uiState is InventoryUiState.Syncing, currentStep > 2)
            SyncStepItem(3, "Vínculo establecido", uiState is InventoryUiState.Success, uiState is InventoryUiState.Success)
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (uiState !is InventoryUiState.Syncing && uiState !is InventoryUiState.Success) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = manualIp,
                    onValueChange = { manualIp = it },
                    placeholder = { Text("IP Manual", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ColorAccent,
                        unfocusedBorderColor = ColorBorder,
                        focusedTextColor = ColorText,
                        unfocusedTextColor = ColorText
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                
                IconButton(
                    onClick = {
                        val permission = Manifest.permission.CAMERA
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            showScanner = true
                        } else {
                            permissionLauncher.launch(permission)
                        }
                    },
                    modifier = Modifier.size(56.dp).background(ColorSurface, RoundedCornerShape(12.dp)).border(1.dp, ColorBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = "Scan QR", tint = ColorAccent)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    if (manualIp.isNotEmpty()) viewModel.setHelperConfig(manualIp, 8080, 8081)
                    else viewModel.syncInventory() 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(if (manualIp.isEmpty()) "AUTO-ESCANEAR" else "VINCULAR IP", fontWeight = FontWeight.Bold)
            }
            
            TextButton(
                onClick = { exportHelperToDownloads(context) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("OBTENER HELPER (EXE)", color = ColorAccent.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            if (currentStep < 3) {
                TextButton(
                    onClick = onSyncComplete,
                    modifier = Modifier.padding(top = 0.dp)
                ) {
                    Text("OMITIR POR AHORA", color = ColorTextMuted, fontSize = 11.sp)
                }
            }
        }

        if (uiState is InventoryUiState.Error) {
            Surface(
                color = Color.Red.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(top = 20.dp)
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

fun exportHelperToDownloads(context: Context) {
    try {
        val fileName = "TenshinBridge.exe"
        val inputStream: InputStream = context.assets.open(fileName)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Toast.makeText(context, "Helper guardado en Descargas", Toast.LENGTH_LONG).show()
            }
        } else {
            @Suppress("DEPRECATION")
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            java.io.FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            Toast.makeText(context, "Helper guardado en Descargas", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error: Mueve el EXE a la carpeta assets del proyecto primero.", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun SyncStepItem(number: Int, label: String, isActive: Boolean, isDone: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .alpha(if (isActive || isDone) 1f else 0.4f)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isDone) ColorGreen else if (isActive) ColorAccent else Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.CheckCircle, null, tint = Color.Black, modifier = Modifier.size(18.dp))
            } else {
                Text(number.toString(), fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 11.sp)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isDone) ColorGreen else if (isActive) ColorText else ColorTextMuted,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )

        if (isActive) {
            Spacer(Modifier.weight(1f))
            CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = ColorAccent)
        }
    }
}
