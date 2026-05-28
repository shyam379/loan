package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = UtilityBlue,
    onPrimary = Color.White,
    primaryContainer = UtilityBlueContainer,
    onPrimaryContainer = UtilityBlue,
    secondary = UtilityBlue,
    onSecondary = Color.White,
    secondaryContainer = UtilitySecondaryBlue,
    onSecondaryContainer = UtilityBlue,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightOutlineVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = UtilityError,
    onError = Color.White,
    errorContainer = UtilityErrorContainer,
    onErrorContainer = UtilityError
)

private val DarkColorScheme = darkColorScheme(
    primary = UtilityBlueDark,
    onPrimary = Color.Black,
    primaryContainer = UtilityBlueContainerDark,
    onPrimaryContainer = Color.White,
    secondary = UtilityBlueDark,
    onSecondary = Color.Black,
    secondaryContainer = UtilityBlueDark.copy(alpha = 0.15f),
    onSecondaryContainer = UtilityBlueDark,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkOutlineVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = UtilityError,
    onError = Color.White,
    errorContainer = UtilityError.copy(alpha = 0.2f),
    onErrorContainer = UtilityError
)

@Composable
fun MyApplicationTheme(
    themePreference: String = "system",
    dynamicColor: Boolean = false, // Set to false to support clean utility theme colors out of the box
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themePreference) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

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
