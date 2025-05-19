package com.progmov.crud.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Añade esto para manejar el estado del tema
data class ThemeState(
    val isDarkTheme: Boolean,
    val useDynamicColor: Boolean
)

val LocalThemeState = staticCompositionLocalOf<ThemeState> {
    error("No ThemeState provided")
}

// Archivo ui/theme/Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightBackground,
    onSurface = LightOnBackground,
    error = ErrorColor
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkBackground,
    onSurface = DarkOnBackground,
    error = ErrorColor
)
@Composable
fun CRUDTheme(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeState.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (themeState.isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeState.isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Función para cambiar el tema
fun updateTheme(darkTheme: Boolean, dynamicColor: Boolean = true) {
    // Esta función está implementada en el ViewModel
}