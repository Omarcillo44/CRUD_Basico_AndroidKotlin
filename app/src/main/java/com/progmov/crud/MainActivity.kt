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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
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
    val context = LocalContext.current
    val productos = remember {
        mutableStateOf(dbManager.obtenerProductos())
    }

    var alertaEliminacion by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    Column (
        modifier = Modifier
            .statusBarsPadding()
    ){
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

            // Botón de añadir como un carácter
            Text(
                text = "➕",
                fontSize = 28.sp,
                modifier = Modifier
                    .clickable {
                        navControlador.navigate("AgregarProducto")
                        //Toast.makeText(context, "Añadir producto", Toast.LENGTH_SHORT).show()
                    }
                    .padding(8.dp)
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

                    // Sombra que da el efecto de elevación de la card
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    // Un tanto obvio
                    shape = RoundedCornerShape(15.dp),
                    // Color de la card
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            // Espaciado interno de la tarjeta
                            .padding(16.dp)
                            // Ajustamos al ancho de la pantalla
                            .fillMaxWidth(),
                        // Centramos en la pantalla
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen simulada
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFB3E5FC), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center // Centrar la imagen dentro del Box
                        ) {
                            ImagenProducto( // metodo para mostrar la imagen
                                rutaImagen = producto.imagen
                            )
                        }

                        // Separación entre la imagen y el resto
                        Spacer(modifier = Modifier.width(16.dp))

                        // Información del producto con layout más flexible
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Nombre y precio
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

                            // Descripción y botones en la misma fila
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

                                // Botones
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✏️",
                                        fontSize = 24.sp,
                                        modifier = Modifier
                                            .clickable {
                                                Toast.makeText(
                                                    context,
                                                    "Editar: ${producto.nombre}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .padding(8.dp)
                                    )
                                    Text(
                                        text = "❌",
                                        fontSize = 24.sp,
                                        modifier = Modifier
                                            .clickable {
                                                productoAEliminar = producto
                                                alertaEliminacion = true
                                                //Toast.makeText(context, "Eliminar: ${producto.nombre}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        //Alerta para confirmar eliminacion de un producto
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
                            productos.value = dbManager.obtenerProductos()  // Actualiza la lista
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
}



// Añade esta función al final de tu archivo
@Preview(showBackground = true)
@Composable
fun VistaProductosPreview() {
    // Creamos datos de ejemplo para la previsualización
    val productosEjemplo = listOf(
        Producto(1, "Champú Hidratante", 12.99, "Champú con fórmula hidratante para todo tipo de cabello", "xd"),
        Producto(2, "Acondicionador Reparador", 14.50, "Repara puntas abiertas y da brillo al cabello", "xd"),
        Producto(3, "Gel de Ducha", 8.75, "Gel de ducha con aroma a lavanda", "xd")
    )

    // Creamos un componente composable que simula nuestra vista real
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn {
            items(productosEjemplo) { producto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    //Sombra que da el efecto de elevación de la card
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    //Un tanto obvio
                    shape = RoundedCornerShape(15.dp),
                    //Color de la card
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            //Espaciado interno de la tarjeta
                            .padding(16.dp)
                            //Ajustamos al ancho de la pantalla
                            .fillMaxWidth(),
                        //centramos en la pantalla
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen simulada
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(Color(0xFFB3E5FC), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center // Centrar la imagen dentro del Box
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.producto), // Imagen desde los recursos
                                contentDescription = "Imagen del producto",
                                modifier = Modifier
                                    .fillMaxSize() // La imagen ocupará todo el espacio del Box
                                    .clip(RoundedCornerShape(8.dp)) // Redondear las esquinas para que coincidan con el fondo
                            )
                        }

                        //Separación entre la imagen y el resto
                        Spacer(modifier = Modifier.width(16.dp))

                        /*Información del producto*/
                        Column{
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
                            Text(
                                text = producto.descripcion,
                                fontSize = 14.sp
                            )
                        }

                        Column {
                            Text(
                                text = "✏️",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .clickable {
                                    }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "❌",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .clickable {
                                    }
                                    .padding(8.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}
