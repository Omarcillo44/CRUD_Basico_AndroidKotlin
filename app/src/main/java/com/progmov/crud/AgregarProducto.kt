package com.progmov.crud

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream



@Composable
fun AgregarProducto(navControlador: NavController) {
    val context = LocalContext.current
    val auxSQLite = DBHelper(LocalContext.current)

    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var precio by rememberSaveable { mutableStateOf("") }
    var imagen by remember { mutableStateOf<Bitmap?>(null) }
    //var imagen by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var rutaImagen by rememberSaveable { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }
    var errorPrecio by remember { mutableStateOf(false) }
    var errorDescripcion by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .statusBarsPadding() // espacio para la barra de estados (hora, bateria, camara etc)
            .navigationBarsPadding() // espacio para los botones de navegacion
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón para regresar a la ventana principal
            Text(
                text = "⬅",
                fontSize = 28.sp,
                modifier = Modifier
                    .clickable {
                        //navControlador.popBackStack()
                        navControlador.navigate("UIPrincipal")
                    }
                    .padding(8.dp)
            )
            Text(
                text = "Agregar producto nuevo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            color = Color.LightGray,
            thickness = 1.dp
        )

        LazyColumn (
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .weight(1f)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Nombre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
            }

            item {
                OutlinedTextField(
                    value = nombre,
                    placeholder = { Text("Titulo del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    // limita la cantidad de letras que se pueden escribir
                    onValueChange = { input ->
                        if (input.length <= 30) {
                            nombre = input
                        }
                        errorNombre = false
                    },
                    // indicardor para mostrar la cantidad de letras disponibles
                    trailingIcon = {
                        Text("${nombre.length}/30")
                    },
                    // cambia el color del textfield cuando el valor es invalido
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
                Text(text = "Precio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
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
                Text(text = "Descripción",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
            }
            item {
                OutlinedTextField(
                    value = descripcion,
                    placeholder = { Text("Detalles del producto") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), // Altura inicial para que se vea grande
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
                Text(text = "Imagen",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp)
                Spacer(modifier = Modifier.height(5.dp))
            }
            // Parte que permite seleccionar la imagen
            item {
                //guarda la accion para mostrarla despues de que se le otorgen permisos
                var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

                // espera la respuesta para poder mostrar la galeria o camara
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) { // parmiso otorgado
                        pendingAction?.invoke()  // Ejecuta la acción que se estaba esperando
                        pendingAction = null
                    } else {
                        Toast.makeText(context, "Permiso denegado. Ve a la configuración para habilitar el permiso.", Toast.LENGTH_LONG).show()
                    }
                }

                // Selector de imagen en la galeria
                val galleryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        // convierte la imagen seleccionada a un bitmap
                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                        imagen = bitmap
                    }
                }

                // Camara para tomar la foto
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
                            // llama al metodo para mostar imagenes pasandole un bitmap
                            ImagenProducto(imagen!!)
                        } ?: //Image(painterResource(id = R.drawable.producto),null)
                        Text("No hay imagen seleccionada.\nSe asignará una por defecto.")
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
                                    ) == PackageManager.PERMISSION_GRANTED) {
                                        action()
                                    } else {
                                        pendingAction = action
                                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                    }
                                } else{
                                    action()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp), // Bordes redondeados
                        ) {
                            Text(text = "Galería",
                                fontSize = 15.sp)
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
                            shape = RoundedCornerShape(16.dp), // Bordes redondeados
                        ) {
                            Text(text = "Cámara",
                                fontSize = 15.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {
                // validaciones para ver que no haya campos vacios
                if (nombre.isNullOrBlank()) {
                    Toast.makeText(context, "Ingrese un nombre para el producto", Toast.LENGTH_SHORT).show()
                    errorNombre = true
                } else if (precio.isNullOrBlank() || precio.toDouble() == 0.0) {
                    Toast.makeText(context, "Ingrese un precio valido para el producto", Toast.LENGTH_SHORT).show()
                    errorPrecio = true
                } else if (descripcion.isNullOrBlank()) {
                    Toast.makeText(context, "Ingrese una descripción para el producto", Toast.LENGTH_SHORT).show()
                    errorDescripcion = true
                } else {
                    // intenta agregar el producto
                    try {
                        // crea la ruta de la imagen
                        imagen?.let {
                            rutaImagen = guardarImagenEnArchivo(context, imagen!!)
                        }
                        // agrega el producto a la base de datos
                        auxSQLite.agregarProducto(
                            nombre,
                            precio.toDouble(),
                            descripcion,
                            rutaImagen
                        )
                        Toast.makeText(context, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Hubo un error, intentelo de nuevo", Toast.LENGTH_SHORT).show()
                    }
                    // limpia los valores asignados
                    nombre = ""
                    precio = ""
                    descripcion = ""
                    imagen = null
                    rutaImagen = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RectangleShape
        ) {
            Text(text = "Agregar producto",
                //fontWeight = FontWeight.Bold,
                fontSize = 20.sp)
        }
    }
}
