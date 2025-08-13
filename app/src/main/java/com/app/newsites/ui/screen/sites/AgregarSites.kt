package com.app.newsites.ui.screen.sites

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AgregarSiteScreen(
    navController: NavController,
    viewModel: SitesViewModel = viewModel()
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var img by remember { mutableStateOf("") }

    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var lastAddress by remember { mutableStateOf<String?>(null) }

    var ubicacionActual by remember { mutableStateOf<LatLng?>(null) }
    val tipos = listOf("Naturaleza", "Cultura", "Gastronomía", "Aventura")
    var tipoSeleccionado by remember { mutableStateOf("") }

    val user = viewModel.usuario.collectAsState()
    val userId = user.value["email"]?.toString() ?: ""


    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        ubicacionActual = LatLng(location.latitude, location.longitude)
                        markerPosition = ubicacionActual
                    } else {
                        Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "No tienes permiso para acceder a la ubicación", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationPermission.launchPermissionRequest()
        }

    }

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(ubicacionActual) {
        ubicacionActual?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 16f))
            markerPosition = ubicacionActual
        }
    }
    val markerState = rememberMarkerState()

    // Aquí se ejecuta el geocoding cuando cambia markerPosition
    LaunchedEffect(markerPosition) {
        markerPosition?.let { latLng ->
            markerState.position = latLng
            val result = getAddressFromLocation(context, latLng.latitude, latLng.longitude)
            if (result != null) {
                Log.d("Geocoder", "Dirección: $result")
                lastAddress = result
            } else {
                Log.d("Geocoder", "No se encontró dirección")
                lastAddress = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar sitio", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = img,
                onValueChange = { img = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = tipoSeleccionado,
                onValueChange = { tipoSeleccionado = it },
                label = { Text("Tipo de sitio") },
                modifier = Modifier.fillMaxWidth()
            )


            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerPosition = latLng
                }
            ) {
                if (markerPosition != null) {
                    Marker(
                        state = markerState,
                        title = "Marcador único"
                    )
                }
            }

            // Muestra la dirección en el campo de texto
            LaunchedEffect(lastAddress) {
                lastAddress?.let {
                    ubicacion = it
                }
            }

            Button(
                onClick = {
                    if (nombre.isBlank() || descripcion.isBlank() || img.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    viewModel.agregarSite(
                        nombre = nombre,
                        ubicacion = ubicacion,
                        coords = GeoPoint(
                            markerState.position.latitude,
                            markerState.position.longitude
                        ),
                        descripcion = descripcion,
                        img = img,
                        tipo = tipoSeleccionado,
                        owner = userId,
                        onSuccess = {
                            Toast.makeText(context, "Sitio guardado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onFailure = {
                            Toast.makeText(context, "Error al guardar el sitio", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar", color = Color.White)
            }
        }
    }
}

suspend fun getAddressFromLocation(context: Context, lat: Double, lng: Double): String? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0)  // Dirección completa
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

