package com.tripbudget.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Cream,
    secondary = Mint,
    onSecondary = MintText,
    background = Cream,
    onBackground = Ink,
    surface = CardWhite,
    onSurface = Ink,
    surfaceVariant = MintLight,
    onSurfaceVariant = MintMuted,
    outline = Divider,
    error = Color(0xFFB3261E),
)

private val DarkColors = darkColorScheme(
    primary = Mint,
    onPrimary = MintText,
    secondary = Teal,
    onSecondary = Cream,
    background = Color(0xFF0A1917),
    onBackground = Color(0xFFE8F1EE),
    surface = Color(0xFF122925),
    onSurface = Color(0xFFE8F1EE),
)

@Composable
fun TripBudgetTheme(
    useDarkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
