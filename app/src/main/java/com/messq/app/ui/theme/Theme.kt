package com.messq.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MessQLightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = CardBackground,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,
    secondary = OrangeDark,
    onSecondary = CardBackground,
    background = WarmWhite,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = Surface,
    onSurfaceVariant = OnSurface,
    outline = Divider,
    error = HighCrowd
)

@Composable
fun MessQTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MessQLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MessQTypography,
        content = content
    )
}
