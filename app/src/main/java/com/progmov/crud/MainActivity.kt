package com.progmov.crud
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavegacion()
        }
    }
}


@Composable
fun AppNavegacion(){
    val navControlador = rememberNavController()

    //definir el host
    NavHost(navControlador, startDestination = "UIPrincipal"){
        composable("UIPrincipal"){
            UIPrincipal(navControlador)
        }
        composable("AgregarProducto"){
            AgregarProducto(navControlador)
        }
        composable("EditarProducto/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId")?.toIntOrNull() ?: -1
            EditarProducto(navControlador, productoId)
        }
    }
}


@Composable
fun UIPrincipal(navControlador: NavController) {
    val auxSQLite = DBHelper(LocalContext.current)
    // Pasamos el dbManager a VistaProductos para que maneje la lógica de la base de datos
    VistaProductos(auxSQLite, navControlador)
}

@Composable
fun VistaProductos(dbManager: DBHelper, navControlador: NavController) {

    // Estados para controlar el menú
    var isFabExpanded by remember { mutableStateOf(false) }
    val menuOptions = listOf("Ayuda", "Tema") // Opciones del menú

    var showHelpDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val productos = remember {
        mutableStateOf(dbManager.obtenerProductos())
    }

    var alertaEliminacion by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    // Contenedor principal con Box para posicionar el FAB
    Box(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()  // Esto manejará correctamente los márgenes
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            // Fila con título y botón de añadir
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
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Agregar producto",
                    modifier = Modifier
                        .size(45.dp)
                        .clickable {
                            navControlador.navigate("AgregarProducto")
                        }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            LazyColumn {
                items(productos.value) { producto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "Apenas vamos en la R, sé paciente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                                    .background(Color(0xFFB3E5FC), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                ImagenProducto(rutaImagen = producto.imagen)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "$${producto.precio}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
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
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Edit,
                                            contentDescription = "Editar producto",
                                            modifier = Modifier
                                                .size(45.dp)
                                                .clickable {
                                                    // Tu lógica de navegación a edición
                                                    navControlador.navigate("EditarProducto/${producto.id}")
                                                }
                                                .padding(8.dp),
                                            tint = MaterialTheme.colorScheme.primary // Color principal del tema
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.Delete, // o Icons.Outlined.Delete
                                            contentDescription = "Eliminar producto",
                                            modifier = Modifier
                                                .size(45.dp)
                                                .clickable {
                                                    productoAEliminar = producto
                                                    alertaEliminacion = true
                                                }
                                                .padding(8.dp),
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (alertaEliminacion && productoAEliminar != null) {
                AlertDialog(
                    onDismissRequest = { alertaEliminacion = false },
                    title = { Text(text = productoAEliminar!!.nombre) },
                    text = { Text("¿Quieres eliminar este producto?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                alertaEliminacion = false
                                dbManager.eliminarProducto(productoAEliminar!!.id)
                                productos.value = dbManager.obtenerProductos()
                            }
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { alertaEliminacion = false }
                        ) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }

// Contenedor para el FAB y opciones
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            // Primero: Botón principal (FAB)
            FloatingActionButton(
                onClick = { isFabExpanded = !isFabExpanded },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.BottomEnd) // Fuerza posición inferior
            ) {
                Icon(
                    imageVector = if (isFabExpanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = if (isFabExpanded) "Cerrar menú" else "Abrir menú"
                )
            }

            //Segundo: Menú de opciones (aparece ENCIMA del FAB cuando está expandido)
            if (isFabExpanded) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 72.dp), // Espacio para evitar superposición con el FAB
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        menuOptions.forEach { option ->
                            ExtendedFloatingActionButton(
                                onClick = {
                                    when (option) {

                                        "Ayuda" -> {
                                            showHelpDialog = true // Mostrar diálogo
                                            isFabExpanded = false // Cerrar menú FAB
                                        }

                                        "Tema" -> Toast.makeText(
                                            context,
                                            "Tema seleccionado", Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    isFabExpanded = false
                                },
                                modifier = Modifier.widthIn(min = 150.dp),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ) {
                                Icon(
                                    imageVector = when (option) {
                                        "Ayuda" -> Icons.Default.Check
                                        "Tema" -> Icons.Default.Check
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = option
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(option)
                            }
                        }
                    }
                }
            }

            // Diálogo de Ayuda
            if (showHelpDialog) {
                AlertDialog(
                    onDismissRequest = { showHelpDialog = false },
                    title = {
                        Text(
                            text = "Muy wenas noches",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            text = "¿Neta muy imbécil como para no saberle?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showHelpDialog = false }
                        ) {
                            Text("Zi")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showHelpDialog = false }
                        ) {
                            Text("Zi x2")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            }

        }
    }
}


