package com.progmov.crud

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.progmov.crud.components.MenuFab
import com.progmov.crud.components.guardarImagenEnArchivo
import com.progmov.crud.database.DBHelper
import com.progmov.crud.ui.theme.ThemeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditarProducto(navControlador: NavController, productoId: Int, themeViewModel: ThemeViewModel) {
    val context = LocalContext.current
    val auxSQLite = DBHelper(context)

    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var imagen by remember { mutableStateOf<Bitmap?>(null) }
    var rutaImagen by rememberSaveable { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }
    var errorPrecio by remember { mutableStateOf(false) }
    var errorDescripcion by remember { mutableStateOf(false) }

    var showHelpDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Cargar los datos del producto existente
    LaunchedEffect(productoId) {
        val productos = auxSQLite.obtenerProductos()
        val producto = productos.find { it.id == productoId }

        producto?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            precio = "%.2f".format(it.precio)
            rutaImagen = it.imagen

            // Cargar la imagen si existe
            if (it.imagen.isNotEmpty() && it.imagen != "placeholder_base64") {
                withContext(Dispatchers.IO) {
                    try {
                        // Si la imagen es una ruta de archivo
                        val file = java.io.File(it.imagen)
                        if (file.exists()) {
                            imagen = BitmapFactory.decodeFile(it.imagen)
                        }
                        // Si la imagen es un Base64
                        else if (it.imagen.startsWith("data:") || it.imagen.length > 100) {
                            try {
                                val base64 = it.imagen.substringAfter("base64,")
                                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                                imagen = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            } catch (e: Exception) {
                                // Manejar error si no es Base64 válido
                            }
                        }
                    } catch (e: Exception) {
                        // Manejar error de carga de imagen
                    }
                }
            }
        } ?: run {
            // Si no se encuentra el producto, volver a la pantalla principal
            Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
            navControlador.navigate("UIPrincipal")
        }
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(start = 16.dp, end = 16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    modifier = Modifier
                        .clickable { navControlador.navigate("UIPrincipal") }
                        .padding(8.dp)
                )
                Text(
                    text = "Editar producto",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                item {
                    CampoTexto("Nombre", nombre, "Título del producto", 30, errorNombre) {
                        nombre = it
                        errorNombre = false
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    CampoTexto(
                        "Precio",
                        precio,
                        "Costo del producto (MXN)",
                        12,
                        errorPrecio,
                        soloDecimal = true
                    ) {
                        precio = it
                        errorPrecio = false
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    CampoTexto(
                        "Descripción",
                        descripcion,
                        "Detalles del producto",
                        150,
                        errorDescripcion,
                        multiline = true
                    ) {
                        descripcion = it
                        errorDescripcion = false
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        SeccionImagen(imagen, rutaImagen) { nuevaImagen -> imagen = nuevaImagen }
                    }
                }
            }

            Row(Modifier.align(Alignment.CenterHorizontally)) {
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Ingrese un nombre para el producto", Toast.LENGTH_SHORT).show()
                            errorNombre = true
                        } else if (precio.isBlank() || precio.toDoubleOrNull() == null || precio.toDouble() <= 0.0) {
                            Toast.makeText(context, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
                            errorPrecio = true
                        } else if (descripcion.isBlank()) {
                            Toast.makeText(context, "Ingrese una descripción", Toast.LENGTH_SHORT).show()
                            errorDescripcion = true
                        } else {
                            try {
                                imagen?.let { rutaImagen = guardarImagenEnArchivo(context, it) }
                                auxSQLite.actualizarProducto(productoId, nombre, precio.toDouble(), descripcion, rutaImagen)
                                Toast.makeText(context, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                                navControlador.navigate("UIPrincipal") {
                                    popUpTo("EditarProducto") { inclusive = true }
                                }
                            } catch (ex: Exception) {
                                Toast.makeText(context, "Error al actualizar producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Actualizar producto", fontSize = 18.sp)
                }
            }
        }

        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            MenuFab(
                onAyudaClick = { showHelpDialog = true },
                onTemaClick = { showThemeDialog = true }
            )
        }
    }

    if (showHelpDialog) DialogoAyuda { showHelpDialog = false }
    if (showThemeDialog) DialogoTema(onClose = { showThemeDialog = false }, themeViewModel = themeViewModel)
}
