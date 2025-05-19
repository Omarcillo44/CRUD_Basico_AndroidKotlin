package com.progmov.crud

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.progmov.crud.components.ImagenProducto
import com.progmov.crud.components.MenuFab
import com.progmov.crud.components.guardarImagenEnArchivo
import com.progmov.crud.database.DBHelper
import com.progmov.crud.ui.theme.CRUDTheme
import com.progmov.crud.ui.theme.ThemeState
import com.progmov.crud.ui.theme.ThemeViewModel

@Composable
fun AgregarProducto(navControlador: NavController, themeViewModel: ThemeViewModel) {
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

    Box(modifier =  Modifier.fillMaxSize().systemBarsPadding()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
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
                    text = "Agregar producto nuevo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                item {
                    CampoTexto("Nombre", nombre, "Título del producto", 30, errorNombre) {
                        nombre = it; errorNombre = false
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
                        true
                    ) {
                        precio = it; errorPrecio = false
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
                        descripcion = it; errorDescripcion = false
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item{

                    Box(modifier = Modifier
                        .align(Alignment.CenterHorizontally)){
                        SeccionImagen(imagen) { nuevaImagen -> imagen = nuevaImagen }
                    }


                }
            }

            Row(Modifier.align(Alignment.CenterHorizontally)){

                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            Toast.makeText(context, "Ingrese un nombre para el producto", Toast.LENGTH_SHORT).show()
                            errorNombre = true
                        } else if (precio.isBlank() || precio.toDoubleOrNull() == 0.0) {
                            Toast.makeText(context, "Ingrese un precio válido", Toast.LENGTH_SHORT).show()
                            errorPrecio = true
                        } else if (descripcion.isBlank()) {
                            Toast.makeText(context, "Ingrese una descripción", Toast.LENGTH_SHORT).show()
                            errorDescripcion = true
                        } else {
                            try {
                                imagen?.let { rutaImagen = guardarImagenEnArchivo(context, it) }
                                auxSQLite.agregarProducto(nombre, precio.toDouble(), descripcion, rutaImagen)
                                Toast.makeText(context, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                                nombre = ""; precio = ""; descripcion = ""; imagen = null; rutaImagen = ""
                            } catch (ex: Exception) {
                                Toast.makeText(context, "Error al agregar producto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Agregar producto", fontSize = 18.sp)
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


@Composable
fun CampoTexto(titulo: String, valor: String, placeholder: String, limite: Int, hayError: Boolean, soloDecimal: Boolean = false, multiline: Boolean = false, onValorCambio: (String) -> Unit) {
    Column {
        Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        OutlinedTextField(
            value = valor,
            onValueChange = {
                if ((soloDecimal && it.matches(Regex("^\\d{0,12}(\\.\\d{0,2})?\$"))) || (!soloDecimal && it.length <= limite)) {
                    onValorCambio(it)
                }
            },
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .height(if (multiline) 120.dp else 56.dp),
            trailingIcon = { Text("${valor.length}/$limite") },
            keyboardOptions = if (soloDecimal) KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal) else KeyboardOptions.Default,
            singleLine = !multiline,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = if (hayError) Color.Red else MaterialTheme.colorScheme.outline,
                unfocusedIndicatorColor = if (hayError) Color.Red else MaterialTheme.colorScheme.outline,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            )
        )
    }
}

@Composable
fun SeccionImagen(imagen: Bitmap?, onImagenSeleccionada: (Bitmap) -> Unit) {
    val context = LocalContext.current
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permisoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) pendingAction?.invoke() else Toast.makeText(context, "Permiso denegado", Toast.LENGTH_LONG).show()
    }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
            onImagenSeleccionada(bitmap)
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let { onImagenSeleccionada(it) }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .background(Color(0xFFE0F7FA)),
            contentAlignment = Alignment.Center
        ) {
            imagen?.let { ImagenProducto(it) } ?: Text("No hay imagen seleccionada")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val accion = { galeriaLauncher.launch("image/*") }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) accion()
                    else {
                        pendingAction = accion
                        permisoLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                } else accion()
            }) {
                Text("Galería")
            }
            Button(onClick = {
                val accion = { camaraLauncher.launch() }
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) accion()
                else {
                    pendingAction = accion
                    permisoLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Cámara")
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewAgregarProducto() {
    val navController = rememberNavController()
    val themeViewModel = ThemeViewModel() // Si tiene parámetros, ajústalos

    CRUDTheme(themeState = ThemeState(isDarkTheme = true, useDynamicColor = false)) { // Reemplaza con tu tema real
        AgregarProducto(navControlador = navController, themeViewModel = themeViewModel)
    }
}
