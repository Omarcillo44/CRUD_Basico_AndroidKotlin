package com.progmov.crud.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class ThemeViewModel : ViewModel() {
    private val _themeState = mutableStateOf(
        ThemeState(
            isDarkTheme = false, // Tema claro por defecto
            useDynamicColor = true // Usar colores dinámicos si están disponibles
        )
    )
    val themeState: State<ThemeState> = _themeState

    /**
     * Activa o desactiva el tema oscuro
     */
    fun setDarkTheme(enabled: Boolean) {
        _themeState.value = _themeState.value.copy(
            isDarkTheme = enabled
        )
    }

    /**
     * Activa o desactiva los colores dinámicos (Material You)
     * Disponible sólo en Android 12+
     */
    fun setDynamicColor(enabled: Boolean) {
        _themeState.value = _themeState.value.copy(
            useDynamicColor = enabled
        )
    }

    /**
     * Alterna entre modo claro y oscuro
     */
    fun toggleDarkMode() {
        setDarkTheme(!_themeState.value.isDarkTheme)
    }
}