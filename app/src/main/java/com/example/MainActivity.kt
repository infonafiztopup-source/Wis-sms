package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DeliveryLogEntity
import com.example.data.local.entities.SmsEventEntity
import com.example.data.local.entities.WebhookEndpointEntity
import com.example.ui.components.WisfyFixedTopNav
import com.example.ui.dialogs.AddEndpointDialog
import com.example.ui.dialogs.DocsDialog
import com.example.ui.dialogs.InspectPayloadDialog
import com.example.ui.dialogs.ProviderProfilesDialog
import com.example.ui.dialogs.SimulateSmsDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EndpointsScreen
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmsFeedScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.WarmSurfaceCard
import com.example.ui.theme.WisfyTheme
import com.example.ui.viewmodel.WisfyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WisfyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            val settings by viewModel.settings.collectAsState()
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = when (settings?.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemDark
            }
            WisfyTheme(darkTheme = isDark) {
                WisfyAppRoot(viewModel = viewModel)
            }
        }
    }
}

enum class OnboardingStep {
    WELCOME,
    PERMISSIONS,
    COMPLETED
}

enum class NavigationTab {
    DASHBOARD,
    SMS_FEED,
    ENDPOINTS,
    SETTINGS
}

@Composable
@Composable
fun WisfyAppRoot(viewModel: WisfyViewModel) {
    var showSplash by remember { mutableStateOf(true) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1450)
        showSplash = false
    }
    Box(modifier = Modifier.fillMaxSize()) {
        WisfyAppContent(viewModel)
        androidx.compose.animation.AnimatedVisibility(
            visible = showSplash,
            exit = androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(320)
            )
        ) {
            WisfySplashScreen()
        }
    }
}

@Composable
private fun WisfyAppContent(viewModel: WisfyViewModel) {
    val settings by viewModel.settings.collectAsState()
    val endpoints by viewModel.endpoints.collectAsState()
    val events by viewModel.events.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val deliveredCount by viewModel.deliveredCount.collectAsState()
    val queuedCount by viewModel.queuedCount.collectAsState()
    val failedCount by viewModel.failedCount.collectAsState()
    val totalCount by viewModel.totalLogsCount.collectAsState()

    var onboardingStep by remember { mutableStateOf(OnboardingStep.WELCOME) }
    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }

    // Dialog state controllers
    var showAddEndpointDialog by remember { mutableStateOf(false) }
    var endpointToEdit by remember { mutableStateOf<WebhookEndpointEntity?>(null) }
    var showSimulateSmsDialog by remember { mutableStateOf(false) }
    var showDocsDialog by remember { mutableStateOf(false) }
    var showProviderProfilesDialog by remember { mutableStateOf(false) }
    var inspectTarget by remember { mutableStateOf<Pair<SmsEventEntity, DeliveryLogEntity?>?>(null) }

    // Check if onboarding completed from saved settings
    val isCompleted = settings?.hasCompletedOnboarding == true

    if (!isCompleted && onboardingStep != OnboardingStep.COMPLETED) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            when (onboardingStep) {
                OnboardingStep.WELCOME -> {
                    WelcomeScreen(
                        onContinue = { onboardingStep = OnboardingStep.PERMISSIONS }
                    )
                }
                OnboardingStep.PERMISSIONS -> {
                    BackHandler { onboardingStep = OnboardingStep.WELCOME }
                    PermissionsScreen(
                        onPermissionsGrantedOrSkipped = {
                            viewModel.setOnboardingCompleted(true)
                            onboardingStep = OnboardingStep.COMPLETED
                        }
                    )
                }
                OnboardingStep.COMPLETED -> Unit
            }
        }
    } else {
        // Back navigation handler to return to Dashboard from other tabs
        if (currentTab != NavigationTab.DASHBOARD) {
            BackHandler {
                currentTab = NavigationTab.DASHBOARD
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = WarmSurface,
            topBar = {
                WisfyFixedTopNav(
                    isDaemonActive = settings?.backgroundServiceActive ?: true,
                    onSimulateClick = { showSimulateSmsDialog = true },
                    onDocsClick = { showDocsDialog = true },
                    onAddEndpointClick = {
                        endpointToEdit = null
                        showAddEndpointDialog = true
                    }
                )
            },
            bottomBar = {
                WisfyBottomBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {

                androidx.compose.animation.AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        val direction = targetState.ordinal > initialState.ordinal
                        (androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(200)) +
                                androidx.compose.animation.slideInHorizontally(
                                    animationSpec = androidx.compose.animation.core.tween(240),
                                    initialOffsetX = { if (direction) it / 6 else -it / 6 }
                                ))
                            .togetherWith(
                                androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(160)) +
                                        androidx.compose.animation.slideOutHorizontally(
                                            animationSpec = androidx.compose.animation.core.tween(200),
                                            targetOffsetX = { if (direction) -it / 6 else it / 6 }
                                        )
                            )
                    },
                    label = "tabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        NavigationTab.DASHBOARD -> {
                            DashboardScreen(
                                endpoints = endpoints,
                                events = events,
                                logs = logs,
                                deliveredCount = deliveredCount,
                                queuedCount = queuedCount,
                                failedCount = failedCount,
                                totalCount = totalCount,
                                isServiceActive = settings?.backgroundServiceActive ?: true,
                                onAddEndpointClick = {
                                    endpointToEdit = null
                                    showAddEndpointDialog = true
                                },
                                onSimulateSmsClick = { showSimulateSmsDialog = true },
                                onOpenDocsClick = { showDocsDialog = true },
                                onOpenProviderProfilesClick = { showProviderProfilesDialog = true },
                                onInspectEvent = { event, log -> inspectTarget = Pair(event, log) }
                            )
                        }
                        NavigationTab.SMS_FEED -> {
                            SmsFeedScreen(
                                events = events,
                                logs = logs,
                                onInspectPayload = { event, log -> inspectTarget = Pair(event, log) },
                                onRetryLog = { logId -> viewModel.retryDelivery(logId) },
                                onResendEvent = { eventId, cb ->
                                    viewModel.resendSmsEvent(eventId) { cb() }
                                }
                            )
                        }
                        NavigationTab.ENDPOINTS -> {
                            EndpointsScreen(
                                endpoints = endpoints,
                                onAddEndpointClick = {
                                    endpointToEdit = null
                                    showAddEndpointDialog = true
                                },
                                onEditEndpointClick = { ep ->
                                    endpointToEdit = ep
                                    showAddEndpointDialog = true
                                },
                                onDeleteEndpointClick = { id -> viewModel.deleteEndpoint(id) },
                                onToggleEndpoint = { ep -> viewModel.toggleEndpoint(ep) },
                                onTestEndpoint = { ep, cb -> viewModel.testEndpoint(ep, cb) }
                            )
                        }
                        NavigationTab.SETTINGS -> {
                            SettingsScreen(
                                settings = settings,
                                eventCount = events.size,
                                onToggleService = { active -> viewModel.setServiceActive(active) },
                                onToggleMaskBody = { mask -> viewModel.setMaskSmsBody(mask) },
                                onToggleForwardRaw = { forward -> viewModel.setForwardRawBody(forward) },
                                onToggleOtpFilter = { filter -> viewModel.setAutoFilterOtp(filter) },
                                onClearData = { viewModel.clearLogsAndQueue() },
                                onExportReport = { cb -> viewModel.exportDiagnosticReport(cb) },
                                onOpenDocs = { showDocsDialog = true },
                                onSelectThemeMode = { mode -> viewModel.setThemeMode(mode) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddEndpointDialog) {
        AddEndpointDialog(
            initialEndpoint = endpointToEdit,
            onDismiss = { showAddEndpointDialog = false },
            onSave = { ep ->
                viewModel.saveEndpoint(ep) {
                    showAddEndpointDialog = false
                }
            },
            onTest = { ep, cb -> viewModel.testEndpoint(ep, cb) }
        )
    }

    if (showSimulateSmsDialog) {
        SimulateSmsDialog(
            onDismiss = { showSimulateSmsDialog = false },
            onSimulate = { sender, body, simSlot ->
                viewModel.simulateIncomingSms(sender, body, simSlot)
            }
        )
    }

    if (showDocsDialog) {
        DocsDialog(onDismiss = { showDocsDialog = false })
    }

    if (showProviderProfilesDialog) {
        ProviderProfilesDialog(onDismiss = { showProviderProfilesDialog = false })
    }

    inspectTarget?.let { (event, log) ->
        InspectPayloadDialog(
            event = event,
            log = log,
            onDismiss = { inspectTarget = null },
            onRetry = { logId ->
                viewModel.retryDelivery(logId)
                inspectTarget = null
            }
        )
    }
}

@Composable
fun WisfyBottomBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    val isDark = com.example.ui.theme.LocalWisfyColors.current.isDark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Linear / Raycast Floating Glass Navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (isDark) 14.dp else 8.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = if (isDark) Color(0x60000000) else Color(0x18000000),
                    spotColor = if (isDark) Color(0x80000000) else Color(0x20000000)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(com.example.ui.theme.LocalWisfyColors.current.surfaceElevated)
                .border(
                    1.dp,
                    com.example.ui.theme.LocalWisfyColors.current.borderSubtle,
                    RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    isSelected = currentTab == NavigationTab.DASHBOARD,
                    onClick = { onTabSelected(NavigationTab.DASHBOARD) },
                    testTag = "nav_dashboard"
                )

                BottomNavItem(
                    icon = Icons.Default.Sms,
                    label = "SMS Feed",
                    isSelected = currentTab == NavigationTab.SMS_FEED,
                    onClick = { onTabSelected(NavigationTab.SMS_FEED) },
                    testTag = "nav_sms_feed"
                )

                BottomNavItem(
                    icon = Icons.Default.Webhook,
                    label = "Endpoints",
                    isSelected = currentTab == NavigationTab.ENDPOINTS,
                    onClick = { onTabSelected(NavigationTab.ENDPOINTS) },
                    testTag = "nav_endpoints"
                )

                BottomNavItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = currentTab == NavigationTab.SETTINGS,
                    onClick = { onTabSelected(NavigationTab.SETTINGS) },
                    testTag = "nav_settings"
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val isDark = com.example.ui.theme.LocalWisfyColors.current.isDark

    if (isSelected) {
        // Active Capsule
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(
                    Brush.verticalGradient(
                        if (isDark) {
                            listOf(
                                Color(0xFF34D399),
                                Color(0xFF087F5B)
                            )
                        } else {
                            listOf(
                                Color(0xFF202A23),
                                Color(0xFF141C17)
                            )
                        }
                    )
                )
                .border(
                    1.dp,
                    if (isDark) Color(0x3334D399) else Color(0x22FFFFFF),
                    RoundedCornerShape(9999.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .testTag(testTag)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = PureWhite,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontFamily = com.example.ui.theme.InterFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite
                )
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag(testTag)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontFamily = com.example.ui.theme.InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

