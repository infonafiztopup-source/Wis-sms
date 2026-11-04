package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import com.example.ui.components.AppEnteringSplash
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.example.ui.components.ThemeSweepRevealOverlay
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
import com.example.ui.theme.PureBlack
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
fun WisfyAppRoot(viewModel: WisfyViewModel) {
    val settings by viewModel.settings.collectAsState()
    val endpoints by viewModel.endpoints.collectAsState()
    val events by viewModel.events.collectAsState()
    val logs by viewModel.logs.collectAsState()

    val deliveredCount by viewModel.deliveredCount.collectAsState()
    val queuedCount by viewModel.queuedCount.collectAsState()
    val failedCount by viewModel.failedCount.collectAsState()
    val totalCount by viewModel.totalLogsCount.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("wisfy_user_prefs", android.content.Context.MODE_PRIVATE) }
    var userDisplayName by remember {
        mutableStateOf(
            prefs.getString("pref_user_name", null)
                ?: android.os.Build.MODEL.ifBlank { "Merchant" }
        )
    }

    var showAppEnteringSplash by remember { mutableStateOf(true) }
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

    if (showAppEnteringSplash) {
        AppEnteringSplash(
            onFinished = { showAppEnteringSplash = false }
        )
    } else if (!isCompleted && onboardingStep != OnboardingStep.COMPLETED) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            when (onboardingStep) {
                OnboardingStep.WELCOME -> {
                    WelcomeScreen(
                        initialName = userDisplayName,
                        onContinue = { chosenName ->
                            userDisplayName = chosenName
                            prefs.edit().putString("pref_user_name", chosenName).apply()
                            onboardingStep = OnboardingStep.PERMISSIONS
                        }
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

        val isDarkTheme = com.example.ui.theme.LocalWisfyColors.current.isDark

        Box(modifier = Modifier.fillMaxSize()) {
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
                        },
                        onSettingsClick = { currentTab = NavigationTab.SETTINGS },
                        onToggleTheme = {
                            val nextMode = if (isDarkTheme) "LIGHT" else "DARK"
                            viewModel.setThemeMode(nextMode)
                        },
                        userName = userDisplayName
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
                        (fadeIn(androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                                slideInHorizontally(
                                    animationSpec = androidx.compose.animation.core.spring(
                                        dampingRatio = 0.86f,
                                        stiffness = 450f
                                    ),
                                    initialOffsetX = { if (direction) (it * 0.16f).toInt() else -(it * 0.16f).toInt() }
                                ) +
                                scaleIn(
                                    initialScale = 0.97f,
                                    animationSpec = androidx.compose.animation.core.tween(200, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                                ))
                            .togetherWith(
                                fadeOut(androidx.compose.animation.core.tween(150, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                                        slideOutHorizontally(
                                            animationSpec = androidx.compose.animation.core.spring(
                                                dampingRatio = 0.86f,
                                                stiffness = 450f
                                            ),
                                            targetOffsetX = { if (direction) -(it * 0.16f).toInt() else (it * 0.16f).toInt() }
                                        ) +
                                        scaleOut(
                                            targetScale = 1.01f,
                                            animationSpec = androidx.compose.animation.core.tween(150)
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

        // Liquid Circular Reveal Theme Sweep Overlay originating from Top-Right Toggle Button
        ThemeSweepRevealOverlay(isDark = isDarkTheme)
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
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ultra-Premium Floating Obsidian Pill Navigation (Salung Reference with Specular Bevel & Active Capsule)
        Box(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 310.dp)
                .fillMaxWidth(0.72f)
                .height(66.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(9999.dp),
                    ambientColor = Color(0x73000000),
                    spotColor = Color(0xA6000000)
                )
                .clip(RoundedCornerShape(9999.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF222226),
                            Color(0xFF131316),
                            Color(0xFF08080A)
                        )
                    )
                )
                .border(
                    1.2.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color(0x38FFFFFF),
                            Color(0x14FFFFFF),
                            Color(0x06FFFFFF)
                        )
                    ),
                    RoundedCornerShape(9999.dp)
                )
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Subtle top chamfer specular highlight line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x20FFFFFF),
                                Color(0x55FFFFFF),
                                Color(0x20FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                val totalWidth = maxWidth
                val tabCount = 4
                val tabWidth = totalWidth / tabCount
                val activePillWidth = 52.dp
                val activePillHeight = 48.dp

                // Sliding white capsule indicator with spring physics
                val targetOffsetX = tabWidth * currentTab.ordinal + (tabWidth - activePillWidth) / 2
                val animatedOffsetX by animateDpAsState(
                    targetValue = targetOffsetX,
                    animationSpec = spring(
                        dampingRatio = 0.78f,
                        stiffness = 420f
                    ),
                    label = "navCircleSlide"
                )

                // White capsule with porcelain finish, inner bevel & micro emerald indicator
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffsetX)
                        .size(width = activePillWidth, height = activePillHeight)
                        .align(Alignment.CenterStart)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color(0x4D000000),
                            ambientColor = Color(0x26000000)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFFF2F2F6)
                                )
                            )
                        )
                        .border(1.dp, Color(0xFFFFFFFF), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Electric Emerald Micro Accent Indicator at the bottom of active pill
                    Box(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .size(width = 8.dp, height = 3.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    )
                }

                // 4 Interactive Tab Slots with testTags & contentDescriptions preserved
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val tabs = listOf(
                        Triple(NavigationTab.DASHBOARD, Icons.Default.Dashboard, "nav_dashboard"),
                        Triple(NavigationTab.SMS_FEED, Icons.Default.Sms, "nav_sms_feed"),
                        Triple(NavigationTab.ENDPOINTS, Icons.Default.Webhook, "nav_endpoints"),
                        Triple(NavigationTab.SETTINGS, Icons.Default.Settings, "nav_settings")
                    )

                    tabs.forEach { (tab, icon, tag) ->
                        val isSelected = currentTab == tab
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.72f,
                                stiffness = 420f
                            ),
                            label = "navIconScale"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        if (currentTab != tab) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            onTabSelected(tab)
                                        }
                                    }
                                )
                                .testTag(tag),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(bottom = if (isSelected) 3.dp else 0.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = tab.name.replace("_", " "),
                                    tint = if (isSelected) Color(0xFF09090B) else Color(0xFF8E8E93),
                                    modifier = Modifier
                                        .size(23.dp)
                                        .scale(iconScale)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

