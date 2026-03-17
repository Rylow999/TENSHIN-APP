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
import com.tenshin.app.data.remote.AutoConfig
import com.tenshin.app.di.NetworkModule
import com.tenshin.app.navigation.NavGraph
import com.tenshin.app.navigation.Screen
import com.tenshin.app.navigation.tenshinNavItems
import com.tenshin.app.ui.theme.*
import com.tenshin.app.ui.components.*
import com.tenshin.app.ui.viewmodel.InventoryViewModel
import com.tenshin.app.ui.viewmodel.InventoryUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleUsbInjectedIp(intent)
        enableEdgeToEdge()
        setContent {
            val inventoryViewModel: InventoryViewModel = viewModel()
            val isHacked by inventoryViewModel.isHacked.collectAsState()
            
            TenshinTheme(isHacked = isHacked) {
                TenshinApp(inventoryViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleUsbInjectedIp(intent)
    }

    private fun handleUsbInjectedIp(intent: Intent?) {
        val ip = intent?.getStringExtra("pc_ip") ?: intent?.data?.getQueryParameter("ip")
        if (ip != null && ip.matches(Regex("""\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}"""))) {
            AutoConfig.saveIp(this, ip)
            NetworkModule.setHelperIp(ip)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenshinApp(inventoryViewModel: InventoryViewModel) {
    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()
    val navController = rememberNavController()
    val context       = LocalContext.current
    val isHacked by inventoryViewModel.isHacked.collectAsState()
    val uiState by inventoryViewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is InventoryUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as InventoryUiState.Error).message,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(isHacked) {
        if (isHacked) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200, 50, 400), -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(500)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        NetworkModule.initializeWithInjectedIp(context)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeRoute = navBackStackEntry?.destination?.route ?: Screen.Sync.route

    var syncPulse by remember { mutableStateOf(false) }

    val activeItem = tenshinNavItems.find { it.id == activeRoute }
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
        gesturesEnabled = activeRoute != Screen.Sync.route,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = surfaceColor,
                modifier = Modifier.width(280.dp),
            ) {
                DrawerContent(
                    items      = tenshinNavItems,
                    activeId   = activeRoute,
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
                        } catch (e: Exception) { }
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (activeRoute != Screen.Sync.route) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp) 
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
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
                                    fontSize      = 10.sp,
                                    color         = primaryColor.copy(alpha = 0.7f),
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily    = if (isHacked) FontFamily.Monospace else FontFamily.Default
                                )
                                Text(
                                    text       = activeItem?.label ?: "Inicio",
                                    fontSize   = 14.sp,
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
                                            color        = if (uiState is InventoryUiState.Syncing) ColorGold else primaryColor.copy(alpha = 0.4f),
                                            cornerRadius = CornerRadius(12.dp.toPx()),
                                            style        = Stroke(width = 1.5.dp.toPx()),
                                        )
                                    }
                                    .clickable(enabled = uiState !is InventoryUiState.Syncing) {
                                        syncPulse = true
                                        inventoryViewModel.syncInventory()
                                        scope.launch {
                                            delay(600)
                                            syncPulse = false
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = if (uiState is InventoryUiState.Syncing) "..." else (if (isHacked) "SYS_SYNC" else "⟳ sync"),
                                    fontSize = 11.sp,
                                    color = if (uiState is InventoryUiState.Syncing) ColorGold else primaryColor,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (activeRoute == Screen.Sync.route) PaddingValues(0.dp) else innerPadding),
            ) {
                NavGraph(
                    navController = navController,
                    portals = portals,
                    inventoryViewModel = inventoryViewModel
                )
            }
        }
    }
}
