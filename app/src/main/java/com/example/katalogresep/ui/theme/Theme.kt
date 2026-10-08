package com.example.katalogresep.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryFixedTerracotta,
    secondary = SecondaryContainerAmber,
    tertiary = TertiaryFixedGreen,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTerracotta,
    primaryContainer = PrimaryContainerTerracotta,
    onPrimary = SurfaceContainerLowest,
    secondary = SecondaryAmber,
    secondaryContainer = SecondaryContainerAmber,
    onSecondaryContainer = OnSecondaryContainerAmber,
    tertiary = TertiaryGreen,
    tertiaryContainer = TertiaryContainerGreen,
    onTertiary = OnTertiaryGreen,
    background = SurfaceWarm,
    surface = SurfaceWarm,
    surfaceVariant = SurfaceContainerHighest,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerHighest = SurfaceContainerHighest,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantWarm,
    outline = OutlineWarm,
    error = ErrorRed,
    errorContainer = ErrorContainerRed,
    onErrorContainer = OnErrorContainerRed
)

@Composable
fun KatalogResepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false so custom M3 ResepKu warm color theme is always used
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

