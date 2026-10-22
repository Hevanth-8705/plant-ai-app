package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenPrimaryDark,
    onPrimary = ForestGreenOnPrimaryDark,
    primaryContainer = ForestGreenContainerDark,
    onPrimaryContainer = ForestGreenOnContainerDark,
    secondary = SageGreenSecondaryDark,
    onSecondary = SageGreenOnSecondaryDark,
    secondaryContainer = SageGreenContainerDark,
    onSecondaryContainer = SageGreenOnContainerDark,
    tertiary = SoftLimeTertiaryDark,
    onTertiary = SoftLimeOnTertiaryDark,
    tertiaryContainer = SoftLimeContainerDark,
    onTertiaryContainer = SoftLimeOnTertiaryContainerDark,
    background = BotanicalBackgroundDark,
    onBackground = BotanicalOnBackgroundDark,
    surface = BotanicalSurfaceDark,
    onSurface = BotanicalOnSurfaceDark,
    surfaceVariant = BotanicalSurfaceVariantDark,
    onSurfaceVariant = BotanicalOnSurfaceVariantDark,
    outline = BotanicalOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = SageGreenSecondary,
    onSecondary = SageGreenOnSecondary,
    secondaryContainer = SageGreenContainer,
    onSecondaryContainer = SageGreenOnContainer,
    tertiary = SoftLimeTertiary,
    onTertiary = SoftLimeOnTertiary,
    tertiaryContainer = SoftLimeContainer,
    onTertiaryContainer = SoftLimeOnTertiaryContainer,
    background = BotanicalBackgroundLight,
    onBackground = BotanicalOnBackgroundLight,
    surface = BotanicalSurfaceLight,
    onSurface = BotanicalOnSurfaceLight,
    surfaceVariant = BotanicalSurfaceVariantLight,
    onSurfaceVariant = BotanicalOnSurfaceVariantLight,
    outline = BotanicalOutlineLight
)

@Composable
fun PlantPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep intentional botanical brand identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    PlantPalTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
