package com.app.newsites.wear.presentation.screen.map

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    onNavigate: ()-> Unit,
    onSiteNavigate: (String) -> Unit
) {
    val context = LocalContext.current

    val sitesState = viewModel.sites.collectAsState()
    val progress = viewModel.progress.collectAsState()
    val showProgress = viewModel.showProgress.collectAsState()

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

    LaunchedEffect(Unit) {
        while (true) {
            try {
                val location = fusedLocationClient
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .await()

                if (location != null) {
                    //cameraPositionState.move(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
                    viewModel.procesarUbicacion(location.latitude, location.longitude, context)
                }
            } catch (e: SecurityException) {
                // El permiso no fue concedido
            }
            delay(5000)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        // Muestra el mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermission.status.isGranted,
            ),
        ) {
            sitesState.value.forEach() { site ->
                val geoPoint = site["coords"] as? GeoPoint
                val nombre = site["nombre"] as? String
                val siteId = site["id"] as String

                if (geoPoint != null){
                    val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                    Marker(
                        onClick = {
                            onSiteNavigate(siteId)
                            true
                      },
                        state = rememberMarkerState(position = latLng),
                        title = nombre
                    )
                }
            }
        }



        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ){
            val animatedProgress by animateFloatAsState(
                targetValue = progress.value,
                animationSpec = tween(durationMillis = 1000), // duración de la animación
                label = "AnimatedProgress"
            )

            val targetAlpha = if (showProgress.value) 1f else 0f
            val animatedAlpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = tween(durationMillis = 500) // Duración de la animación
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                color = Color(0xFFE53935),
                trackColor = Color.White,
                modifier = Modifier
                    .height(8.dp)
                    .weight(1f)
                    .alpha(animatedAlpha)
                    .padding(horizontal = 10.dp),
            )

            FloatingActionButton(
                onClick = onNavigate,
                containerColor = Color(0xFFE53935),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
                    .padding(4.dp)
            ) {
                Icon(Icons.Rounded.AccountCircle, contentDescription = "Perfil", tint = Color.White)
            }

        }
    }


}