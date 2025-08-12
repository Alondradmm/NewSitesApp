package com.app.newsites.ui.screen.Perfil

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.data.DataStoreClass
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncuestaScreen(
    navController: NavController,
    viewModel: PerfilViewModel  = viewModel()//Obtener los datos
){
    val context = LocalContext.current
    val prefs = remember { DataStoreClass(context) }
    var email by remember { mutableStateOf("") }

    // Campos para encuesta
    var asistenciaEspecial by remember { mutableStateOf(false) }

    val idiomas = listOf("Espanol", "Ingles", "Frances")
    val intereses = listOf("Cultura", "Naturaleza", "Gastronomia", "Aventura")
    val pagos = listOf("Tarjeta", "Efectivo")

    val seleccionIdiomas = remember { mutableStateMapOf<String, Boolean>() }
    val seleccionIntereses = remember { mutableStateMapOf<String, Boolean>() }
    val seleccionPago = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(Unit) {
        email = prefs.currentUser.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Encuesta de preferencias", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        }
    ) { paddingValues ->

        //Ejemplo para el formulario (modificar)
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¿Necesitas asistencia especial?")
            Switch(
                checked = asistenciaEspecial,
                onCheckedChange = { asistenciaEspecial = it }
            )
            Divider()//lineas grices

            Text("Idiomas que hablas:")
            idiomas.forEach { idioma ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = seleccionIdiomas[idioma] ?: false,
                        onCheckedChange = { seleccionIdiomas[idioma] = it }
                    )
                    Text(idioma)
                }
            }
            Divider()
            Text("Intereses:")
            intereses.forEach { interes ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = seleccionIntereses[interes] ?: false,
                        onCheckedChange = { seleccionIntereses[interes] = it }
                    )
                    Text(interes)
                }
            }
            Divider()
            Text("Métodos de pago preferidos:")
            pagos.forEach { metodo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = seleccionPago[metodo] ?: false,
                        onCheckedChange = { seleccionPago[metodo] = it }
                    )
                    Text(metodo)
                }
            }
            Divider()
            Button(
                onClick = {
                    val idiomasSeleccionados = seleccionIdiomas.filterValues { it }.keys.toList()
                    val interesesSeleccionados = seleccionIntereses.filterValues { it }.keys.toList()
                    val pagosSeleccionados = seleccionPago.filterValues { it }.keys.toList()

                    if (email.isBlank()) {
                        Toast.makeText(context, "Error: usuario no identificado", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.encuestaMejora(
                        email = email,
                        asistenciaEspecial = asistenciaEspecial,
                        comunicacion = idiomasSeleccionados,
                        intereses = interesesSeleccionados,
                        pago = pagosSeleccionados,

                        onSuccess = {
                            Toast.makeText(context, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = {
                            Toast.makeText(context, "Error al guardar: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))

            ){
                Text("Guardar preferencias", color = Color.White)
            }

        }
    }
}