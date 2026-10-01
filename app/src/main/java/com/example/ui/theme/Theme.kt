package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val BgrDarkColorScheme = darkColorScheme(
    primary = BgrGold,
    onPrimary = BgrDarkBackground,
    primaryContainer = BgrGoldDark,
    onPrimaryContainer = BgrGoldLight,
    
    secondary = BgrEmerald,
    onSecondary = BgrDarkBackground,
    secondaryContainer = BgrEmeraldDark,
    onSecondaryContainer = BgrEmeraldLight,
    
    tertiary = BgrCyanElectric,
    onTertiary = BgrDarkBackground,
    tertiaryContainer = BgrViolet,
    onTertiaryContainer = BgrVioletLight,
    
    background = BgrDarkBackground,
    onBackground = BgrTextPrimary,
    
    surface = BgrDarkSurface,
    onSurface = BgrTextPrimary,
    surfaceVariant = BgrDarkSurfaceVariant,
    onSurfaceVariant = BgrTextSecondary,
    
    outline = BgrBorder,
    outlineVariant = BgrBorderLight,
    
    error = BgrRed,
    onError = BgrTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for premier Web3 fintech experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = BgrDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BgrDarkBackground.toArgb()
                window.navigationBarColor = BgrDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
