package com.progmov.crud.components
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuFab(
    onAyudaClick: () -> Unit,
    onTemaClick: () -> Unit,
) {
    var isFabExpanded by remember { mutableStateOf(false) }
    val menuOptions = listOf("Ayuda", "Tema")

    Box() { // Box padre que ocupa toda la pantalla

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isFabExpanded) {
                    menuOptions.forEach { option ->
                        ExtendedFloatingActionButton(
                            onClick = {
                                when (option) {
                                    "Ayuda" -> onAyudaClick()
                                    "Tema" -> onTemaClick()
                                }
                                isFabExpanded = false
                            },
                            modifier = Modifier.widthIn(min = 150.dp),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Icon(
                                imageVector = when (option) {
                                    "Ayuda" -> Icons.Default.Info
                                    "Tema" -> Icons.Default.Build
                                    else -> Icons.Default.Info
                                },
                                contentDescription = option
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = if (isFabExpanded) "Cerrar menú" else "Abrir menú"
                    )
                }
            }
        }
    }