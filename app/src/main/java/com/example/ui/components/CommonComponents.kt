package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalWisfyColors
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WarmBorderVariant
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.WarmSurfaceCard
import kotlinx.coroutines.delay

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE,
    GHOST,
    DESTRUCTIVE
}

/**
 * Premium Linear / Raycast Style Action Button with Smooth Tactile Scale Animation
 */
@Composable
fun WisfyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    height: Dp = 46.dp,
    testTag: String = "wisfy_button"
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 500f),
        label = "btnScale"
    )

    val isDark = LocalWisfyColors.current.isDark
    val shape = RoundedCornerShape(12.dp)

    val (bgBrush, borderBrush, textColor) = when (variant) {
        ButtonVariant.PRIMARY -> {
            if (!enabled) {
                Triple(
                    SolidColor(if (isDark) Color(0xFF243329) else Color(0xFFE8E4DA)),
                    SolidColor(Color.Transparent),
                    if (isDark) Color(0xFF778579) else Color(0xFF969B91)
                )
            } else {
                Triple(
                    Brush.verticalGradient(
                        if (isDark) {
                            listOf(Color(0xFF34D399), Color(0xFF087F5B))
                        } else {
                            listOf(Color(0xFF202A23), Color(0xFF141C17))
                        }
                    ),
                    SolidColor(Color.Transparent),
                    PureWhite
                )
            }
        }
        ButtonVariant.SECONDARY -> {
            Triple(
                SolidColor(if (isDark) Color(0xFF19231C) else Color(0xFFF0EEE7)),
                SolidColor(if (isDark) Color(0xFF29372D) else Color(0xFFE6E1D7)),
                TextPrimary
            )
        }
        ButtonVariant.OUTLINE -> {
            Triple(
                SolidColor(Color.Transparent),
                SolidColor(if (isDark) Color(0xFF3A4A3E) else Color(0xFFD5CFC3)),
                TextPrimary
            )
        }
        ButtonVariant.GHOST -> {
            Triple(
                SolidColor(Color.Transparent),
                SolidColor(Color.Transparent),
                TextSecondary
            )
        }
        ButtonVariant.DESTRUCTIVE -> {
            Triple(
                SolidColor(if (isDark) Color(0xFF7F1D1D).copy(alpha = 0.4f) else Color(0xFFFEE2E2)),
                SolidColor(if (isDark) Color(0xFF991B1B) else Color(0xFFFCA5A5)),
                if (isDark) Color(0xFFFCA5A5) else Color(0xFFDC2626)
            )
        }
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .clip(shape)
            .background(bgBrush)
            .border(1.dp, borderBrush, shape)
            .pointerInput(enabled && !loading) {
                if (enabled && !loading) {
                    while (true) {
                        awaitPointerEventScope {
                            awaitFirstDown(requireUnconsumed = false)
                            isPressed = true
                            waitForUpOrCancellation()
                            isPressed = false
                        }
                    }
                }
            }
            .clickable(
                enabled = enabled && !loading,
                onClick = onClick
            )
            .padding(horizontal = 18.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = textColor
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontFamily = InterFontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    letterSpacing = (-0.2).sp
                )
            }
        }
    }
}

/**
 * Modern Linear / Raycast Style Card with Subtle Border & Shadow
 */
@Composable
fun WisfyCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    elevation: Dp = 1.dp,
    borderColor: Color = WarmBorder,
    backgroundColor: Color = WarmSurfaceCard,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val clickableMod = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .then(
                if (elevation > 0.dp) {
                    Modifier.shadow(elevation, shape, ambientColor = Color(0x0C000000), spotColor = Color(0x10000000))
                } else Modifier
            )
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(clickableMod)
    ) {
        content()
    }
}

/**
 * Backward-compatible AppleGlassCard updated with refined design tokens
 */
@Composable
fun AppleGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    WisfyCard(
        modifier = modifier,
        cornerRadius = cornerRadius,
        elevation = elevation,
        borderColor = WarmBorder,
        backgroundColor = WarmSurfaceCard,
        onClick = onClick,
        content = content
    )
}

/**
 * Backward-compatible AppleGlossyButton wrapping WisfyButton
 */
@Composable
fun AppleGlossyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isSecondary: Boolean = false,
    testTag: String = "apple_glossy_button"
) {
    WisfyButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = if (isSecondary) ButtonVariant.SECONDARY else ButtonVariant.PRIMARY,
        icon = icon,
        enabled = enabled,
        testTag = testTag
    )
}

/**
 * Feature Tile for Onboarding & Dialogs
 */
@Composable
fun AppleFeatureRow(
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor)
                .border(1.dp, iconColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = InterFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                letterSpacing = (-0.2).sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontFamily = InterFontFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

/**
 * Status Pill Badge with Theme-Aware Subtle Accents
 */
@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val isDark = LocalWisfyColors.current.isDark

    val (bgColor, textColor, dotColor, label) = when (status.lowercase()) {
        "delivered" -> {
            if (isDark) Quad(Color(0xFF064E3B).copy(alpha = 0.5f), Color(0xFF34D399), Color(0xFF10B981), "Delivered")
            else Quad(Color(0xFFECFDF5), Color(0xFF047857), Color(0xFF059669), "Delivered")
        }
        "parsed_candidate" -> {
            if (isDark) Quad(Color(0xFF064E3B).copy(alpha = 0.4f), Color(0xFF6EE7B7), Color(0xFF10B981), "Parsed Payment")
            else Quad(Color(0xFFECFDF5), Color(0xFF065F46), Color(0xFF10B981), "Parsed Payment")
        }
        "duplicate", "message_duplicate_suppressed", "transaction_duplicate_suppressed" -> {
            if (isDark) Quad(Color(0xFF4C1D95).copy(alpha = 0.4f), Color(0xFFC4B5FD), Color(0xFFA78BFA), "Duplicate")
            else Quad(Color(0xFFF3E8FF), Color(0xFF6D28D9), Color(0xFF7C3AED), "Duplicate")
        }
        "queued" -> {
            if (isDark) Quad(Color(0xFF78350F).copy(alpha = 0.4f), Color(0xFFFCD34D), Color(0xFFF59E0B), "Queued")
            else Quad(Color(0xFFFFFBEB), Color(0xFFB45309), Color(0xFFD97706), "Queued")
        }
        "sending" -> {
            if (isDark) Quad(Color(0xFF1E3A8A).copy(alpha = 0.4f), Color(0xFF93C5FD), Color(0xFF3B82F6), "Sending...")
            else Quad(Color(0xFFEFF6FF), Color(0xFF1D4ED8), Color(0xFF2563EB), "Sending...")
        }
        "filtered", "filtered_privacy" -> {
            if (isDark) Quad(Color(0xFF312E81).copy(alpha = 0.4f), Color(0xFFA5B4FC), Color(0xFF6366F1), "Filtered")
            else Quad(Color(0xFFEEF2FF), Color(0xFF4338CA), Color(0xFF4F46E5), "Filtered")
        }
        "failed" -> {
            if (isDark) Quad(Color(0xFF7F1D1D).copy(alpha = 0.4f), Color(0xFFFCA5A5), Color(0xFFEF4444), "Failed")
            else Quad(Color(0xFFFEF2F2), Color(0xFFB91C1C), Color(0xFFDC2626), "Failed")
        }
        else -> {
            if (isDark) Quad(Color(0xFF1E293B), Color(0xFF94A3B8), Color(0xFF64748B), status.replaceFirstChar { it.uppercase() })
            else Quad(Color(0xFFF1F5F9), Color(0xFF475569), Color(0xFF64748B), status.replaceFirstChar { it.uppercase() })
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(bgColor)
            .border(1.dp, dotColor.copy(alpha = 0.25f), RoundedCornerShape(9999.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontFamily = InterFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Animated Number Counter for Stats & Metrics
 */
@Composable
fun AnimatedCounterText(
    value: Int = 0,
    count: Int = value,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = TextPrimary,
    style: TextStyle = TextStyle(
        fontFamily = InterFontFamily,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color
    )
) {
    val targetVal = if (count != 0) count else value
    AnimatedContent(
        targetState = targetVal,
        transitionSpec = {
            if (targetState > initialState) {
                slideInVertically { height -> height } + fadeIn() togetherWith
                        slideOutVertically { height -> -height } + fadeOut()
            } else {
                slideInVertically { height -> -height } + fadeIn() togetherWith
                        slideOutVertically { height -> height } + fadeOut()
            }
        },
        label = "counterAnim",
        modifier = modifier
    ) { targetCount ->
        Text(
            text = targetCount.toString(),
            style = style
        )
    }
}

/**
 * Live Pulsing Activity Dot
 */
@Composable
fun PulsingStatusDot(
    isActive: Boolean = true,
    modifier: Modifier = Modifier,
    color: Color = EmeraldGreen,
    activeColor: Color = color,
    size: Dp = 10.dp,
    pulseScale: Float = 1.8f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(1f + (1f - alpha) * (pulseScale - 1f))
                    .clip(CircleShape)
                    .background(activeColor.copy(alpha = (1f - alpha) * 0.4f))
            )
        }
        Box(
            modifier = Modifier
                .size((size.value * 0.65f).dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor else Color(0xFF94A3B8))
        )
    }
}

/**
 * Monospace Code / Secret Snippet Box with Animated Copy Feedback
 */
@Composable
fun CopySnippetBox(
    textToCopy: String,
    displayText: String = textToCopy,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    val context = LocalContext.current
    val colors = LocalWisfyColors.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            isCopied = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            Text(
                text = label,
                fontFamily = InterFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colors.surfaceHighlight)
                .border(1.dp, WarmBorder, RoundedCornerShape(10.dp))
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Wisfy", textToCopy))
                    isCopied = true
                }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 12.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            AnimatedContent(
                targetState = isCopied,
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "copyIcon"
            ) { copied ->
                if (copied) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Copied",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copied",
                            fontFamily = InterFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Premium Empty State
 */
@Composable
fun WisfyEmptyState(
    icon: ImageVector,
    title: String,
    description: String = "",
    subtitle: String = description,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = LocalWisfyColors.current
    val descText = if (subtitle.isNotBlank()) subtitle else description

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.surfaceHighlight)
                .border(1.dp, WarmBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            fontFamily = InterFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = descText,
            fontFamily = InterFontFamily,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(18.dp))
            WisfyButton(
                text = actionText,
                onClick = onActionClick,
                variant = ButtonVariant.SECONDARY,
                height = 38.dp
            )
        }
    }
}

@Composable
fun BrandTag(text: String, modifier: Modifier = Modifier) {
    val colors = LocalWisfyColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(colors.surfaceHighlight)
            .border(1.dp, WarmBorder, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(EmeraldGreen)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontFamily = InterFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            letterSpacing = 0.5.sp
        )
    }
}

/**
 * Pure Borderless Dynamic Brand Logo (Dark / Light)
 */
@Composable
fun WisfyBrandShape(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Wisfy Logo"
) {
    val isDark = LocalWisfyColors.current.isDark
    val resId = if (isDark) R.drawable.wisfy_shape_dark else R.drawable.wisfy_shape_light
    Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        modifier = modifier
    )
}

@Composable
fun WisfyLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    logoSize: Dp = 32.dp,
    cornerRadius: Dp = 11.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        WisfyBrandShape(
            modifier = Modifier.size(logoSize),
            contentDescription = "Wisfy Logo"
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
