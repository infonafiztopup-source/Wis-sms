package com.example

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WisfyBrandShape
import com.example.ui.theme.InterFontFamily

@Composable
fun WisfySplashScreen() {
    val entrance = remember { Animatable(0.82f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, animationSpec = tween(680, easing = FastOutSlowInEasing))
    }
    val pulse = rememberInfiniteTransition(label = "wisfySplashPulse")
    val haloAlpha by pulse.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(1050, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wisfyHalo"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0B100D), Color(0xFF111A13), Color(0xFF0B100D))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(218.dp)
                .alpha(haloAlpha)
                .background(
                    Brush.radialGradient(listOf(Color(0xFF34D399), Color(0x0034D399))),
                    CircleShape
                )
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(108.dp).scale(entrance.value),
                contentAlignment = Alignment.Center
            ) {
                WisfyBrandShape(modifier = Modifier.size(86.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "WISFY",
                fontFamily = InterFontFamily,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.2.sp,
                color = Color(0xFFF2F5EF),
                modifier = Modifier.alpha((entrance.value - 0.82f) / 0.18f)
            )
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = "SMS GATEWAY",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.1.sp,
                color = Color(0xFF6EE7B7).copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                modifier = Modifier.size(width = 46.dp, height = 2.dp),
                color = Color(0xFF34D399),
                trackColor = Color(0xFF26372B)
            )
        }
    }
}
