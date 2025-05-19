package com.progmov.crud
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import com.progmov.crud.ui.theme.CRUDTheme
import com.progmov.crud.ui.theme.ThemeState
import com.progmov.crud.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val currentThemeState = themeViewModel.themeState.value

            CRUDTheme(themeState = currentThemeState) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavegacion(themeViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavegacion(themeViewModel: ThemeViewModel) {
    val navControlador = rememberNavController()
    NavHost(navController = navControlador, startDestination = "UIPrincipal") {
        composable("UIPrincipal") {
            UIPrincipal(navControlador, themeViewModel)
        }
        composable("AgregarProducto") {
            AgregarProducto(navControlador)
        }
        composable("EditarProducto/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: -1
            EditarProducto(navControlador, productoId)
        }
    }
}

@Composable
fun UIPrincipal(navControlador: NavController, themeViewModel: ThemeViewModel) {
    val auxSQLite = DBHelper(LocalContext.current)
    VistaProductos(auxSQLite, navControlador, themeViewModel)
}

@Composable
fun VistaProductos(dbManager: DBHelper, navControlador: NavController, themeViewModel: ThemeViewModel) {
    var isFabExpanded by remember { mutableStateOf(false) }
    val menuOptions = listOf("Ayuda", "Tema")
    var showHelpDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val productos = remember { mutableStateOf(dbManager.obtenerProductos()) }

    var alertaEliminacion by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var showThemeDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Productos disponibles",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar producto",
                    modifier = Modifier
                        .size(45.dp)
                        .clickable { navControlador.navigate("AgregarProducto") }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn {
                items(productos.value) { producto ->
                    ProductoCard(
                        producto = producto,
                        onEditar = { navControlador.navigate("EditarProducto/${producto.id}") },
                        onEliminar = { productoAEliminar = producto; alertaEliminacion = true },
                        onClick = { Toast.makeText(context, "Apenas vamos en la R, sé paciente", Toast.LENGTH_SHORT).show() }
                    )
                }
            }

            if (alertaEliminacion && productoAEliminar != null) {
                ConfirmarEliminacionDialog(
                    producto = productoAEliminar!!,
                    onConfirmar = {
                        alertaEliminacion = false
                        dbManager.eliminarProducto(productoAEliminar!!.id)
                        productos.value = dbManager.obtenerProductos()
                    },
                    onCancelar = { alertaEliminacion = false }
                )
            }
        }

        // Menú FAB
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isFabExpanded) {
                    menuOptions.forEach { option ->
                        ExtendedFloatingActionButton(
                            onClick = {
                                when (option) {
                                    "Ayuda" -> showHelpDialog = true
                                    "Tema" -> showThemeDialog = true
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

        if (showHelpDialog) {
            DialogoAyuda { showHelpDialog = false }
        }

        if (showThemeDialog) {
            DialogoTema(
                onClose = { showThemeDialog = false },
                themeViewModel = themeViewModel
            )
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                ImagenProducto(rutaImagen = producto.imagen)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = producto.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$${producto.precio}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.descripcion,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier
                                .size(45.dp)
                                .clickable(onClick = onEditar)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar",
                            modifier = Modifier
                                .size(45.dp)
                                .clickable(onClick = onEliminar)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmarEliminacionDialog(
    producto: Producto,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(text = producto.nombre) },
        text = { Text("¿Quieres eliminar este producto?") },
        confirmButton = {
            TextButton(onClick = onConfirmar) { Text("Eliminar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
fun DialogoAyuda(onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("Centro de Ayuda", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Text("¿Qué tipo de asistencia necesitas?", style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(onClick = onClose) { Text("Guía Rápida") }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Contactar Soporte") }
        }
    )
}
@Composable
fun DialogoTema(
    onClose: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("Seleccionar tema", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                // Opción de tema oscuro/claro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            themeViewModel.toggleTheme()
                        }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = themeViewModel.themeState.value.isDarkTheme,
                        onClick = { themeViewModel.toggleTheme() }
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Modo oscuro",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Opción de colores dinámicos (solo para Android 12+)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                themeViewModel.setDynamicColor(!themeViewModel.themeState.value.useDynamicColor)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = themeViewModel.themeState.value.useDynamicColor,
                            onClick = {
                                themeViewModel.setDynamicColor(!themeViewModel.themeState.value.useDynamicColor)
                            }
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = "Colores dinámicos",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Aceptar", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}


// Añade esto al FINAL del archivo:
@Preview(name = "Light Theme")
@Composable
fun PreviewProductoCard2() {
    CRUDTheme(themeState = ThemeState(isDarkTheme = false, useDynamicColor = false)) {
        ProductoCard(
            producto = Producto(
                id = 1,
                nombre = "Ejemplo",
                precio = 9.99,
                descripcion = "Producto de ejemplo",
                imagen = ""
            ),
            onEditar = {},
            onEliminar = {},
            onClick = {}
        )
    }
}
@Preview(name = "Dark Theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewProductoCard() {
    CRUDTheme(themeState = ThemeState(isDarkTheme = true, useDynamicColor = false)) {
        ProductoCard(
            producto = Producto(
                id = 1,
                nombre = "Ejemplo",
                precio = 9.99,
                descripcion = "Producto de ejemplo",
                imagen = ""
            ),
            onEditar = {},
            onEliminar = {},
            onClick = {}
        )
    }
}