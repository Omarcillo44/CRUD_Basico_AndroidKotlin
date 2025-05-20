package com.progmov.crud.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    private val _themeState = mutableStateOf(
        ThemeState(
            isDarkTheme = false, // Tema claro por defecto
            useDynamicColor = true
        )
    )
    val themeState = _themeState

    fun setDarkTheme(enabled: Boolean) {
        _themeState.value = _themeState.value.copy(
            isDarkTheme = enabled
        )
    }

    fun setDynamicColor(enabled: Boolean) {
        _themeState.value = _themeState.value.copy(
            useDynamicColor = enabled
        )
    }
}