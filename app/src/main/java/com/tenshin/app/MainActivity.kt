package com.tenshin.app

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200, 50, 400), -1))
            } else {
                vibrator.vibrate(500)
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
                modifier = Modifier.width(260.dp),
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
            }
        },
    ) {
        Scaffold(
            containerColor = backgroundColor,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(surfaceColor)
                        .drawBehind {
                            drawLine(
                                color = if (isHacked) ColorHackerGreen else ColorBorder,
                                start = androidx.compose.ui.geometry.Offset(0f, size.height),
                                end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .padding(horizontal = 16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier
                                .clickable { scope.launch { drawerState.open() } }
                                .padding(4.dp),
                        ) {
                            listOf(20f, 14f, 20f).forEach { width ->
                                Box(
                                    Modifier
                                        .width(width.dp)
                                        .height(2.dp)
                                        .background(primaryColor, RoundedCornerShape(1.dp))
                                )
                            }
                        }

                        Column(Modifier.weight(1f)) {
                            Text(
                                text          = if (isHacked) "HÖLLVANIA 1999" else "TENSHIN",
                                fontSize      = 11.sp,
                                color         = primaryColor.copy(alpha = 0.7f),
                                letterSpacing = 2.sp,
                                lineHeight    = 11.sp,
                                fontFamily    = if (isHacked) FontFamily.Monospace else FontFamily.Default
                            )
                            Text(
                                text       = activeItem?.label ?: "Inicio",
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = onSurfaceColor,
                                lineHeight = 17.sp,
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .background(syncBg, RoundedCornerShape(8.dp))
                                .drawBehind {
                                    drawRoundRect(
                                        color        = primaryColor.copy(alpha = 0.27f),
                                        cornerRadius = CornerRadius(8.dp.toPx()),
                                        style        = Stroke(width = 1.dp.toPx()),
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
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = if (isHacked) "SYS_SYNC" else "⟳ sync",
                                fontSize = 11.sp,
                                color = primaryColor,
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
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                NavGraph(
                    navController = navController,
                    portals = portals
                )
            }
        }
    }
}
