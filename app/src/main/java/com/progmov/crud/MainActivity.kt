package com.progmov.crud
import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.ui.tooling.preview.Preview
import com.progmov.crud.components.MenuFab
import com.progmov.crud.components.ProductoCard
import com.progmov.crud.models.Producto
import com.progmov.crud.database.DBHelper
import com.progmov.crud.ui.theme.CRUDTheme
import com.progmov.crud.ui.theme.ThemeState
import com.progmov.crud.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = PreferenceHelper(applicationContext)
        val isDarkTheme = prefs.leerEstadoModoOscuro()

        // Asegúrate de acceder a tu ViewModel así:
        val themeViewModel: ThemeViewModel by viewModels()

        // Actualiza el estado antes de setContent
        themeViewModel.setDarkTheme(isDarkTheme)

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
            AgregarProducto(navControlador, themeViewModel)
        }
        composable("EditarProducto/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: -1
            EditarProducto(navControlador, productoId, themeViewModel)
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
                        onClick = { Toast.makeText(context, "Producto #${producto.id}", Toast.LENGTH_SHORT).show() }
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

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 16.dp) // Padding generoso
        ) {
            MenuFab(
                onAyudaClick = { showHelpDialog = true },
                onTemaClick = { showThemeDialog = true }
            )
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
            Text("Ayuda", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text("Instrucciones para el uso del catálogo:", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))

                Text("• Deslice hacia arriba o hacia abajo para explorar los productos.")

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permite editar los datos del producto seleccionado.")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permite eliminar un producto. Se solicitará confirmación.")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abre el menú lateral con opciones como ayuda y personalización de tema.")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClose) {
                Text("Entendido")
            }
        }
    )
}



@Composable
fun DialogoTema(
    onClose: () -> Unit,
    themeViewModel: ThemeViewModel
) {
    val context: Context = LocalContext.current
    val preferenciaModoOscuro = remember { PreferenceHelper(context) }

    var estadoModoOscuro by remember { mutableStateOf(preferenciaModoOscuro.leerEstadoModoOscuro()) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text("Seleccionar tema", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column {
                // Opción de tema claro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            themeViewModel.setDarkTheme(false)
                            estadoModoOscuro = false
                            preferenciaModoOscuro.guardarEstadoModoOscuro(false) // Guardar cambio
                        }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = !estadoModoOscuro,
                        onClick = {
                            themeViewModel.setDarkTheme(false)
                            estadoModoOscuro = false
                            preferenciaModoOscuro.guardarEstadoModoOscuro(false) // Guardar cambio
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = "Modo claro",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Opción de tema oscuro
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            themeViewModel.setDarkTheme(true)
                            estadoModoOscuro = true
                            preferenciaModoOscuro.guardarEstadoModoOscuro(true) // Guardar cambio
                        }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = estadoModoOscuro,
                        onClick = {
                            themeViewModel.setDarkTheme(true)
                            estadoModoOscuro = true
                            preferenciaModoOscuro.guardarEstadoModoOscuro(true) // Guardar cambio
                        }
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

// Previews
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