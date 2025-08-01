package com.app.newsites.ui.screen.map

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = viewModel()
) {

    val sitesState = viewModel.sites.collectAsState()

    val context = LocalContext.current
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 14f))
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

    val isDarkTheme = isSystemInDarkTheme()

    val mapStyleOptions = remember {
        if (isDarkTheme) {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark)
        } else {
            null // estilo por defecto
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()

                if (location != null) {
                    viewModel.procesarUbicacion(location.latitude, location.longitude, context)
                }
            } catch (e: SecurityException) {
                // El permiso no fue concedido
            }

            delay(5000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        },
        bottomBar = {
            NavigationBar (windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        navController.navigate("map") {
                            popUpTo("map") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Map,
                            contentDescription = "Mapa"
                        )
                    },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("sites") {
                            popUpTo("sites") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Store,
                            contentDescription = "Sites"
                        )
                    },
                    label = { Text("Mis Sites") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("perfil") {
                            popUpTo("perfil") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Rounded.Person,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Perfil") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {  },
                properties = MapProperties(
                    isMyLocationEnabled = locationPermission.status.isGranted,
                    mapStyleOptions = mapStyleOptions
                ),
            ) {


                sitesState.value.forEach() { site ->
                    val geoPoint = site["coords"] as? GeoPoint
                    val nombre = site["nombre"] as? String

                    if (geoPoint != null){
                        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                        Marker(
                            state = rememberMarkerState(position = latLng),
                            title = nombre
                        )
                    }

                }
            }

            Card (
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row (
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.newsites),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .height(40.dp)
                    )
                    Text(
                        text = "SP: 1000",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 20.sp,
                        color = Color(0xFFE53935)
                    )
                }

            }

        }
    }
}
