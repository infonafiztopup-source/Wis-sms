package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.WisfyElevation
import com.example.ui.theme.WisfyShapes
import com.example.ui.theme.WisfySpacing
import com.example.ui.theme.WisfyTypography

/**
 * 1. WisfyHeroCard — Dark graphite gradient card with circuit/line corner texture,
 * masked ID pill, big number with faded trailing digits, and 4 circular metallic buttons.
 */
@Composable
fun WisfyHeroCard(
    pillText: String,
    label: String,
    mainValue: String,
    subValue: String? = null,
    onSimulateClick: () -> Unit,
    onAddEndpointClick: () -> Unit,
    onProvidersClick: () -> Unit,
    onDocsClick: () -> Unit,
    modifier: Modifier = Modifier,
    isServiceActive: Boolean = true
) {
    val colors = LocalWisfyColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = WisfyElevation.heroShadow,
                shape = WisfyShapes.HeroCard,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x40000000)
            )
            .clip(WisfyShapes.HeroCard)
            .background(
                Brush.verticalGradient(
                    listOf(colors.heroGradientTop, colors.heroGradientBottom)
                )
            )
            .border(1.dp, Color(0x1AFFFFFF), WisfyShapes.HeroCard)
    ) {
        // Subtle decorative technical circuit lines in corners
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawHeroCircuitTexture(colors.heroTexture)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            // Top: Masked ID Pill (Reference: "**** 6780" with green micro-icon)
            Row(
                modifier = Modifier
                    .clip(WisfyShapes.Pill)
                    .background(colors.heroPillBg)
                    .border(1.dp, Color(0x1AFFFFFF), WisfyShapes.Pill)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isServiceActive) colors.emerald else colors.statusPending)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pillText,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Muted label
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF9E9E9E),
                letterSpacing = 0.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Large Semibold Number (Reference: trailing digits fade to gray)
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = mainValue,
                    fontFamily = InterFontFamily,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PureWhite,
                    letterSpacing = (-1.0).sp
                )
                if (subValue != null) {
                    Text(
                        text = subValue,
                        fontFamily = InterFontFamily,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0x8CFFFFFF),
                        letterSpacing = (-1.0).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Row of 4 Circular Metallic Action Buttons (Reference: Top up, Send, Withdraw, History)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WisfyMetalButton(
                    icon = Icons.Default.Add,
                    label = "Endpoint",
                    onClick = onAddEndpointClick,
                    testTag = "hero_action_endpoint"
                )
                WisfyMetalButton(
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    label = "Simulate",
                    onClick = onSimulateClick,
                    testTag = "hero_action_simulate"
                )
                WisfyMetalButton(
                    icon = Icons.Default.Check,
                    label = "Providers",
                    onClick = onProvidersClick,
                    testTag = "hero_action_providers"
                )
                WisfyMetalButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    label = "Docs",
                    onClick = onDocsClick,
                    testTag = "hero_action_docs"
                )
            }
        }
    }
}

/**
 * 2. WisfyMetalButton — Circular metallic gradient button (white → silver)
 * with a 1dp light rim, dark centered icon, and muted label below.
 */
@Composable
fun WisfyMetalButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "metal_button"
) {
    val colors = LocalWisfyColors.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 450f),
        label = "metalPress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(48.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = CircleShape,
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x59000000)
                )
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(colors.metalTop, colors.metalBottom)
                    )
                )
                .border(1.2.dp, Color(0xF0FFFFFF), CircleShape)
                .pointerInput(Unit) {
                    while (true) {
                        awaitPointerEventScope {
                            awaitFirstDown(requireUnconsumed = false)
                            isPressed = true
                            waitForUpOrCancellation()
                            isPressed = false
                        }
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            // Turned-metal top specular glint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x99FFFFFF),
                                Color.Transparent
                            )
                        )
                    )
            )

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF111113),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB0B0B5),
            letterSpacing = 0.1.sp
        )
    }
}

/**
 * 3. WisfyLayeredCard — The 3-depth layer system:
 * Canvas background → Outer card (surface) → Inner card (white surfaceCard block).
 */
@Composable
fun WisfyLayeredCard(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = EmeraldGreen,
    iconBg: Color = Color(0xFFFFFFFF),
    title: String,
    onChevronClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalWisfyColors.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(WisfyShapes.OuterCard)
            .background(colors.surface)
            .border(1.dp, colors.border, WisfyShapes.OuterCard)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Row: Icon tile, title, and optional chevron square
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    if (icon != null) {
                        WisfyIconTile(
                            icon = icon,
                            tint = iconTint,
                            containerColor = iconBg
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = title,
                        fontFamily = InterFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (onChevronClick != null) {
                    WisfyChevronButton(onClick = onChevronClick)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inner Card Block (pure white nested container)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(WisfyShapes.InnerCard)
                    .background(colors.surfaceCard)
                    .border(1.dp, colors.border, WisfyShapes.InnerCard)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    content()
                }
            }
        }
    }
}

/**
 * 4. WisfyIconTile — White rounded-square tile (14dp radius) with colored icon.
 */
@Composable
fun WisfyIconTile(
    icon: ImageVector,
    tint: Color = EmeraldGreen,
    containerColor: Color = Color(0xFFFFFFFF),
    modifier: Modifier = Modifier,
    size: Dp = 42.dp
) {
    val colors = LocalWisfyColors.current
    val isDark = colors.isDark

    Box(
        modifier = modifier
            .size(size)
            .shadow(
                elevation = 3.dp,
                shape = WisfyShapes.IconTile,
                ambientColor = Color(0x18000000),
                spotColor = Color(0x26000000)
            )
            .clip(WisfyShapes.IconTile)
            .background(
                Brush.verticalGradient(
                    if (isDark) {
                        listOf(Color(0xFF26262B), Color(0xFF19191D))
                    } else {
                        listOf(Color(0xFFFFFFFF), Color(0xFFF7F7FA))
                    }
                )
            )
            .border(
                1.dp,
                if (isDark) Color(0x2EFFFFFF) else Color(0xFFE4E4E9),
                WisfyShapes.IconTile
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle tinted halo matching the icon's color
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(tint.copy(alpha = 0.16f), Color.Transparent)
                    )
                )
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

/**
 * 5. WisfyChevronButton — Small white square button with chevron arrow.
 */
@Composable
fun WisfyChevronButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "chevron_btn"
) {
    val colors = LocalWisfyColors.current
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(WisfyShapes.ChevronButton)
            .background(colors.surfaceCard)
            .border(1.dp, colors.border, WisfyShapes.ChevronButton)
            .clickable(onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View detail",
            tint = colors.textSecondary,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * 6. WisfyCtaButton — Full-width pill, dark graphite gradient, white "+ label", soft shadow.
 */
@Composable
fun WisfyCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    testTag: String = "cta_button"
) {
    val colors = LocalWisfyColors.current
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 450f),
        label = "ctaPress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .scale(scale)
            .shadow(
                elevation = if (enabled) WisfyElevation.ctaShadow else 0.dp,
                shape = WisfyShapes.Pill,
                ambientColor = Color(0x33000000),
                spotColor = Color(0x40000000)
            )
            .clip(WisfyShapes.Pill)
            .background(
                if (enabled) {
                    Brush.verticalGradient(
                        listOf(colors.ctaGradientTop, colors.ctaGradientBottom)
                    )
                } else {
                    Brush.verticalGradient(
                        listOf(Color(0xFF8E8E93), Color(0xFF6C6C74))
                    )
                }
            )
            .border(1.dp, Color(0x26FFFFFF), WisfyShapes.Pill)
            .pointerInput(enabled && !loading) {
                while (true) {
                    awaitPointerEventScope {
                        awaitFirstDown(requireUnconsumed = false)
                        isPressed = true
                        waitForUpOrCancellation()
                        isPressed = false
                    }
                }
            }
            .clickable(
                enabled = enabled && !loading,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.buttonOnPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = colors.buttonOnPrimary,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontFamily = InterFontFamily,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.buttonOnPrimary,
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}

/**
 * 7. WisfyThinProgress — 4dp fully rounded progress track & fill.
 */
@Composable
fun WisfyThinProgress(
    progress: Float, // 0f to 1f
    fillColor: Color = EmeraldGreen,
    trackColor: Color = Color(0xFFEBEBEB),
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(WisfyShapes.Pill)
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(WisfyShapes.Pill)
                .background(fillColor)
        )
    }
}

/**
 * 8. WisfyDeltaChip — Tinted status / delta pill (e.g. "+112.00%" or "Delivered").
 */
@Composable
fun WisfyDeltaChip(
    text: String,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
    leadingArrow: Boolean = true,
    customColor: Color? = null,
    customBg: Color? = null
) {
    val colors = LocalWisfyColors.current
    val fg = customColor ?: if (isPositive) colors.emerald else colors.statusFailed
    val bg = customBg ?: if (isPositive) colors.emeraldLight else Color(0xFFFEE2E2)

    Row(
        modifier = modifier
            .clip(WisfyShapes.Pill)
            .background(bg)
            .border(1.dp, fg.copy(alpha = 0.3f), WisfyShapes.Pill)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingArrow) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        } else {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(fg)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

/**
 * 9. WisfyPillBars Chart — Fully rounded graphite bars with subtle diagonal hatch pattern
 * and faint dashed grid lines.
 */
@Composable
fun WisfyPillBars(
    values: List<Float>, // 0f to 1f normalized
    labels: List<String>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 100.dp
) {
    val colors = LocalWisfyColors.current
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            // Dashed Grid Lines (Reference)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridY = size.height * 0.5f
                drawLine(
                    color = colors.chartGrid,
                    start = Offset(0f, gridY),
                    end = Offset(size.width, gridY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )
            }

            // Staggered Graphite Pill Bars
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                values.forEachIndexed { index, fraction ->
                    val barFill = (fraction * animatedProgress.value).coerceIn(0.08f, 1f)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                            .fillMaxHeight(barFill)
                            .clip(WisfyShapes.Pill)
                            .background(colors.chartBar)
                    ) {
                        // Subtle diagonal hatch pattern
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawHatchLines(Color(0x1AFFFFFF))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Month / Day Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    text = label,
                    fontFamily = InterFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textMuted,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * 10. WisfyTransactionRow — White row with circular avatar + small status badge on corner,
 * title + subtitle, bold amount on the right + time below.
 */
@Composable
fun WisfyTransactionRow(
    title: String,
    subtitle: String,
    amount: String,
    time: String,
    statusColor: Color = EmeraldGreen,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalWisfyColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(WisfyShapes.InnerCard)
            .background(colors.surfaceCard)
            .border(1.dp, colors.border, WisfyShapes.InnerCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f).padding(end = 10.dp)
        ) {
            // Avatar with corner status indicator badge
            Box(modifier = Modifier.size(42.dp)) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceHighlight)
                        .border(1.dp, colors.border, CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.take(2).uppercase(),
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                // Corner status badge (Reference: small green dot/arrow badge)
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .border(2.dp, colors.surfaceCard, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontFamily = InterFontFamily,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amount,
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp,
                color = colors.textMuted
            )
        }
    }
}

/**
 * 11. WisfySmileLoader — Canvas arc that rotates as a spinner then eases into a smile curve,
 * with green accent end-cap.
 */
@Composable
fun WisfySmileLoader(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp
) {
    val colors = LocalWisfyColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "smileAnim")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 70f,
        targetValue = 190f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweepAngle"
    )

    Canvas(modifier = modifier.size(size)) {
        val stroke = strokeWidth.toPx()
        val diameter = size.toPx() - stroke
        val arcSize = Size(diameter, diameter)
        val topLeft = Offset(stroke / 2f, stroke / 2f)

        drawArc(
            color = colors.buttonPrimary,
            startAngle = rotation,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        // Green accent tip
        drawArc(
            color = colors.emerald,
            startAngle = rotation + sweepAngle - 20f,
            sweepAngle = 20f,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
    }
}

// Helper canvas routines for technical circuit textures
private fun DrawScope.drawHeroCircuitTexture(color: Color) {
    val strokeWidth = 1.dp.toPx()
    // Top right subtle circuit angle
    val pathTR = Path().apply {
        moveTo(size.width - 60f, 10f)
        lineTo(size.width - 30f, 10f)
        lineTo(size.width - 15f, 25f)
        lineTo(size.width - 15f, 50f)
    }
    drawPath(pathTR, color, style = Stroke(strokeWidth))
    drawCircle(color, 2.5.dp.toPx(), Offset(size.width - 60f, 10f))

    // Bottom left subtle angle
    val pathBL = Path().apply {
        moveTo(15f, size.height - 45f)
        lineTo(15f, size.height - 25f)
        lineTo(35f, size.height - 10f)
        lineTo(60f, size.height - 10f)
    }
    drawPath(pathBL, color, style = Stroke(strokeWidth))
    drawCircle(color, 2.5.dp.toPx(), Offset(60f, size.height - 10f))
}

private fun DrawScope.drawHatchLines(color: Color) {
    val step = 8.dp.toPx()
    var x = -size.height
    while (x < size.width) {
        drawLine(
            color = color,
            start = Offset(x, 0f),
            end = Offset(x + size.height, size.height),
            strokeWidth = 1.dp.toPx()
        )
        x += step
    }
}
