package com.progmov.crud.components

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import coil.compose.rememberAsyncImagePainter
import com.progmov.crud.R


//Metodo para mostrar la imagen de los productos guardados como base 64
// Recibe imagen en base64
@Composable
fun ImagenProductoBase64(base64Str: String) {
    // convierte la imagen base64 a un bitmap
    val bitmap = remember(base64Str) { base64ToBitmap(base64Str) }
    bitmap?.let {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Imagen del producto",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)) // Redondear las esquinas para que coincidan con el fondo
        )
    }
}

// metodo para mostrar la imagen de producto por defecto
// No recibe parametros
@Composable
fun ImagenProducto(){
    Image(
        painter = painterResource(id = R.drawable.producto), // Reemplaza con tu imagen por defecto
        contentDescription = "Imagen del producto",
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)) // Redondear las esquinas para que coincidan con el fondo
    )
}

//Metodo para mostrar la imagen de los productos en un tumbnail 100x100
// Recibe la ruta donde se encuentra la imagen
@Composable
fun ImagenProducto(rutaImagen: String) {
    if (rutaImagen.startsWith("data:image")) {
        ImagenProductoBase64(rutaImagen)
        return
    } else if (rutaImagen.isEmpty()) {
        ImagenProducto()
        return
    } else {
        Image(
            painter = rememberAsyncImagePainter(rutaImagen),
            contentDescription = "Imagen del producto",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

// metodo para mostrar la imagen
// Recibe imagen en bitmap
@Composable
fun ImagenProducto(bitmap: Bitmap) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp)) // Redondear las esquinas para que coincidan con el fondo
    )
}

// Metodo para guardar una imagen en una carpeta de la aplicacion (en /data/data)
fun guardarImagenEnArchivo(context: Context, bitmap: Bitmap): String {
    // asigna un nombre y crea el archivo
    val nombreArchivo = "IMG_${System.currentTimeMillis()}.jpg"
    val archivo = File(context.filesDir, nombreArchivo)
    // guarda la imagen como jpeg
    val outputStream = FileOutputStream(archivo)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    return archivo.absolutePath // regresa la ruta absoluta
}

//Metodo para decodificar la imagen en base64
fun base64ToBitmap(base64Str: String): Bitmap? {
    try {
        val cleanBase64 = base64Str.substringAfter(",") // limpiar por si trae encabezado
        val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        return null // Devuelve null si falla la decodificación
    }
}

// Metodo para convertir Bitmap a Base64
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
