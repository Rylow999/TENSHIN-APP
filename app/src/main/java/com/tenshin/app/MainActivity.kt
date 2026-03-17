package com.tenshin.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tenshin.app.di.NetworkModule
import com.tenshin.app.navigation.NavGraph
import com.tenshin.app.navigation.Screen
import com.tenshin.app.navigation.tenshinNavItems
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.components.*
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val inventoryViewModel: InventoryViewModel = viewModel()
            val isHacked by inventoryViewModel.isHacked.collectAsState()
            
            TenshinTheme(isHacked = isHacked) {
                TenshinApp(inventoryViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenshinApp(inventoryViewModel: InventoryViewModel) {
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val scope         = rememberCoroutineScope()
    val navController = rememberNavController()
    val context       = LocalContext.current
    val isHacked by inventoryViewModel.isHacked.collectAsState()
    
    LaunchedEffect(isHacked) {
        if (isHacked) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200, 50, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        NetworkModule.initializeWithInjectedIp(context)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeSection = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var syncPulse by remember { mutableStateOf(false) }

    val activeItem = tenshinNavItems.find { it.id == activeSection }
    val portals    = tenshinNavItems.filter { it.id != "home" }

    val syncBg by animateColorAsState(
        targetValue   = if (syncPulse) ColorAccentGlow else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = spring(),
        label         = "syncBg",
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = surfaceColor,
                modifier = Modifier.width(280.dp),
            ) {
                DrawerContent(
                    items      = tenshinNavItems,
                    activeId   = activeSection,
                    onNavigate = { id ->
                        scope.launch { drawerState.close() }
                        navController.navigate(id) {
                            popUpTo(Screen.Home.route)
                            launchSingleTop = true
                        }
                    },
                )
                
                Spacer(Modifier.weight(1f))
                
                TextButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:support@tenshin.app")
                            putExtra(Intent.EXTRA_SUBJECT, "Tenshin App Error Report")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle case where no email app is installed
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Report Error", fontSize = 12.sp)
                }
            }
        },
    ) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp) 
                        .background(surfaceColor)
                        .drawBehind {
                            drawLine(
                                color = if (isHacked) ColorHackerGreen else ColorBorder,
                                start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .statusBarsPadding(),
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { scope.launch { drawerState.open() } }
                                .padding(8.dp),
                        ) {
                            listOf(24f, 16f, 24f).forEach { width ->
                                Box(
                                    Modifier
                                        .width(width.dp)
                                        .height(2.5.dp)
                                        .background(primaryColor, RoundedCornerShape(1.2.dp))
                                )
                            }
                        }

                        Column(Modifier.weight(1f)) {
                            Text(
                                text          = if (isHacked) "HÖLLVANIA 1999" else "TENSHIN",
                                fontSize      = 12.sp,
                                color         = primaryColor.copy(alpha = 0.7f),
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily    = if (isHacked) FontFamily.Monospace else FontFamily.Default
                            )
                            Text(
                                text       = activeItem?.label ?: "Inicio",
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = onSurfaceColor,
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(syncBg, RoundedCornerShape(12.dp))
                                .drawBehind {
                                    drawRoundRect(
                                        color        = primaryColor.copy(alpha = 0.4f),
                                        cornerRadius = CornerRadius(12.dp.toPx()),
                                        style        = Stroke(width = 1.5.dp.toPx()),
                                    )
                                }
                                .clickable {
                                    syncPulse = true
                                    inventoryViewModel.syncInventory()
                                    scope.launch {
                                        delay(600)
                                        syncPulse = false
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = if (isHacked) "SYS_SYNC" else "⟳ sync",
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                NavGraph(
                    navController = navController,
                    portals = portals
                )
            }
        }
    }
}
