package com.progmov.crud


import android.content.Context

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE)

    fun guardarEstadoModoOscuro(isDarkMode: Boolean) {
        prefs.edit().putBoolean("modo_oscuro", isDarkMode).apply()
    }

    fun leerEstadoModoOscuro(): Boolean {
        return prefs.getBoolean("modo_oscuro", false)
    }

}