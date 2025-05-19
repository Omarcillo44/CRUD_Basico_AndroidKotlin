// ThemeViewModel.kt
package com.progmov.crud.ui.theme // <- MISMO PAQUETE que ThemeState

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    private val _themeState = mutableStateOf(
        ThemeState( // <- Usa la misma definición
            isDarkTheme = false,
            useDynamicColor = true
        )
    )
    val themeState = _themeState

    fun toggleTheme() {
        _themeState.value = _themeState.value.copy(
            isDarkTheme = !_themeState.value.isDarkTheme
        )
    }


    fun setDynamicColor(enabled: Boolean) {
        _themeState.value = _themeState.value.copy(
            useDynamicColor = enabled
        )
    }

}