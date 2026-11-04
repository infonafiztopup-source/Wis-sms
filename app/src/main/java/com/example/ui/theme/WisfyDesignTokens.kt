package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object WisfyShapes {
    val HeroCard = RoundedCornerShape(28.dp)
    val OuterCard = RoundedCornerShape(24.dp)
    val InnerCard = RoundedCornerShape(18.dp)
    val IconTile = RoundedCornerShape(14.dp)
    val ChevronButton = RoundedCornerShape(12.dp)
    val Pill = RoundedCornerShape(9999.dp)
    val BottomSheetTop = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    val ActionCircle = RoundedCornerShape(9999.dp)
}

object WisfySpacing {
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val cardPadding: Dp = 16.dp
    val sectionGap: Dp = 20.dp
    val screenPadding: Dp = 20.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
}

object WisfyElevation {
    val none: Dp = 0.dp
    val card: Dp = 0.dp // Hairline border instead of card shadow in Light theme
    val heroShadow: Dp = 12.dp
    val ctaShadow: Dp = 10.dp
    val navShadow: Dp = 14.dp
}
