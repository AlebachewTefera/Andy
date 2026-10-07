package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Dark theme anchored on Syntax Deep Navy & Electric Accents
private val DarkColorScheme = darkColorScheme(
    primary = SyntaxBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0C3C78),
    onPrimaryContainer = SyntaxCyanSoft,
    secondary = SyntaxGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF5A3E08),
    onSecondaryContainer = SyntaxGoldLight,
    tertiary = SyntaxGreen,
    onTertiary = Color.White,
    background = DarkBg,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder
)

// Light theme anchored on Crisp High-Tech White, Syntax Blue, Green and Gold
private val LightColorScheme = lightColorScheme(
    primary = SyntaxBlue,
    onPrimary = Color.White,
    primaryContainer = SyntaxCyanSoft,
    onPrimaryContainer = SyntaxNavy,
    secondary = SyntaxGold,
    onSecondary = Color.Black,
    secondaryContainer = SyntaxGoldLight,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = SyntaxGreen,
    onTertiary = Color.White,
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = CardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight
)

// Premium futuristic morphic shapes with squircle curvature
val CuteShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = CuteShapes,
        content = content
    )
}
