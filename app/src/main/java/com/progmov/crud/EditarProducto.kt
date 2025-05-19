package com.progmov.crud

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.progmov.crud.components.ImagenProducto
import com.progmov.crud.components.guardarImagenEnArchivo
import com.progmov.crud.database.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditarProducto(navControlador: NavController, productoId: Int) {
    val context = LocalContext.current
    val auxSQLite = DBHelper(context)
    
    // Variables para almacenar los datos del producto
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var imagen by remember { mutableStateOf<Bitmap?>(null) }
    var rutaImagen by rememberSaveable { mutableStateOf("") }
    
    var errorNombre by remember { mutableStateOf(false) }
    var errorPrecio by remember { mutableStateOf(false) }
    var errorDescripcion by remember { mutableStateOf(false) }
    
    // Cargar los datos del producto existente
    LaunchedEffect(productoId) {
        val productos = auxSQLite.obtenerProductos()
        val producto = productos.find { it.id == productoId }
        
        producto?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            precio = it.precio.toString()
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

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón para regresar
            Text(
                text = "⬅",
                fontSize = 28.sp,
                modifier = Modifier
                    .clickable {
                        navControlador.navigate("UIPrincipal")
                    }
                    .padding(8.dp)
            )
            Text(
                text = "Editar producto",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp
        )

        LazyColumn(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Nombre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            item {
                OutlinedTextField(
                    value = nombre,
                    placeholder = { Text("Título del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = { input ->
                        if (input.length <= 30) {
                            nombre = input
                        }
                        errorNombre = false
                    },
                    trailingIcon = {
                        Text("${nombre.length}/30")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = if (errorNombre) Color.Red else MaterialTheme.colorScheme.outline,
                        unfocusedIndicatorColor = if (errorNombre) Color.Red else MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Precio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            item {
                OutlinedTextField(
                    value = precio,
                    placeholder = { Text("Costo del producto (en MXN)") },
                    onValueChange = { input ->
                        // Solo permite decimales
                        if (input.matches(Regex("^\\d{0,12}(\\.\\d{0,2})?\$"))) {
                            precio = input
                        }
                        errorPrecio = false
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = if (errorPrecio) Color.Red else MaterialTheme.colorScheme.outline,
                        unfocusedIndicatorColor = if (errorPrecio) Color.Red else MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            item {
                OutlinedTextField(
                    value = descripcion,
                    placeholder = { Text("Detalles del producto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    onValueChange = { input ->
                        val lineCount = input.count { it == '\n' } + 1
                        if (input.length <= 150 && lineCount <= 5) {
                            descripcion = input
                        }
                        errorDescripcion = false
                    },
                    trailingIcon = {
                        Text("${descripcion.length}/150")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = if (errorDescripcion) Color.Red else MaterialTheme.colorScheme.outline,
                        unfocusedIndicatorColor = if (errorDescripcion) Color.Red else MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Imagen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
            // Parte que permite seleccionar la imagen
            item {
                var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        pendingAction?.invoke()
                        pendingAction = null
                    } else {
                        Toast.makeText(context, "Permiso denegado. Ve a la configuración para habilitar el permiso.", Toast.LENGTH_LONG).show()
                    }
                }

                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                        imagen = bitmap
                    }
                }

                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap: Bitmap? ->
                    bitmap?.let {
                        imagen = bitmap
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .background(Color(0xFFB3E5FC)),
                        contentAlignment = Alignment.Center
                    ) {
                        imagen?.let {
                            ImagenProducto(imagen!!)
                        } ?: run {
                            if (rutaImagen.isNotEmpty() && rutaImagen != "placeholder_base64") {
                                // Si hay ruta pero no imagen cargada, mostrar un mensaje
                                Text("Imagen guardada anteriormente")
                            } else {
                                Text("No hay imagen seleccionada.\nSe asignará una por defecto.")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                val action = {
                                    galleryLauncher.launch("image/*")
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        action()
                                    } else {
                                        pendingAction = action
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    }
                                } else {
                                    action()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                text = "Galería",
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = {
                                val action = {
                                    cameraLauncher.launch()
                                }
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    action()
                                } else {
                                    pendingAction = action
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                text = "Cámara",
                                fontSize = 15.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {
                // Validaciones
                if (nombre.isBlank()) {
                    Toast.makeText(context, "Ingrese un nombre para el producto", Toast.LENGTH_SHORT).show()
                    errorNombre = true
                } else if (precio.isBlank() || precio.toDouble() == 0.0) {
                    Toast.makeText(context, "Ingrese un precio válido para el producto", Toast.LENGTH_SHORT).show()
                    errorPrecio = true
                } else if (descripcion.isBlank()) {
                    Toast.makeText(context, "Ingrese una descripción para el producto", Toast.LENGTH_SHORT).show()
                    errorDescripcion = true
                } else {
                    try {
                        // Si se seleccionó una nueva imagen, guardarla
                        imagen?.let {
                            rutaImagen = guardarImagenEnArchivo(context, imagen!!)
                        }
                        
                        // Actualizar el producto en la base de datos
                        auxSQLite.actualizarProducto(
                            productoId,
                            nombre,
                            precio.toDouble(),
                            descripcion,
                            rutaImagen
                        )
                        
                        Toast.makeText(context, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                        navControlador.navigate("UIPrincipal")
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Error al actualizar: ${ex.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RectangleShape
        ) {
            Text(
                text = "Actualizar producto",
                fontSize = 20.sp
            )
        }
    }
}