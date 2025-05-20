// Archivo ui/theme/Color.kt
package com.progmov.crud.ui.theme

import androidx.compose.ui.graphics.Color

// Colores primarios (tonos teal/verde azulado)
val Teal50 = Color(0xFF48A69F)
val Teal100 = Color(0xFF399288)
val Teal200 = Color(0xFF0D887F)
val Teal300 = Color(0xFF056C65)
val Teal400 = Color(0xFF00544E)

// Colores secundarios #1 (tonos amarillo/naranja claro)
val Amber50 = Color(0xFFFFCD6F)
val Amber100 = Color(0xFFEEB343)
val Amber200 = Color(0xFFDE9916)
val Amber300 = Color(0xFFB07609)
val Amber400 = Color(0xFF895A00)

// Colores secundarios #2 (tonos rojo/rosa)
val Red50 = Color(0xFFFB6D74)
val Red100 = Color(0xFFEA424A)
val Red200 = Color(0xFFDA161F)
val Red300 = Color(0xFFAD0811)
val Red400 = Color(0xFF860007)

// Colores complementarios (tonos naranja)
val Orange50 = Color(0xFFFFAE6F)
val Orange100 = Color(0xFFEE8D43)
val Orange200 = Color(0xFFDE6D16)
val Orange300 = Color(0xFFB05109)
val Orange400 = Color(0xFF893B00)

// Colores para tema claro
val LightPrimary = Teal200       // Color primario principal
val LightOnPrimary = Color.White
val LightSecondary = Amber200    // Color secundario principal
val LightOnSecondary = Color.Black
val LightTertiary = Red200       // Color terciario/acento
val LightOnTertiary = Color.White
val LightBackground = Color(0xFFF8F8F8)  // Fondo claro
val LightOnBackground = Color(0xFF1C1B1F)
val LightSurface = Color.White
val LightOnSurface = Color(0xFF1C1B1F)

// Colores para tema oscuro
val DarkPrimary = Teal50         // Versión más clara del primario para tema oscuro
val DarkOnPrimary = Color.Black
val DarkSecondary = Amber50      // Versión más clara del secundario
val DarkOnSecondary = Color.Black
val DarkTertiary = Red50         // Versión más clara del terciario/acento
val DarkOnTertiary = Color.Black
val DarkBackground = Color(0xFF121212)  // Fondo oscuro
val DarkOnBackground = Color.White
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color.White

// Colores adicionales
val ErrorColor = Red300
val SuccessColor = Teal100
val WarningColor = Orange200
val InfoColor = Amber100