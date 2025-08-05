package com.app.newsites.ui.screen.map

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.newsites.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = viewModel(),
    emailUsuario: String?,
) {
    val context = LocalContext.current
    val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraPositionState = rememberCameraPositionState()
    var isMapLoaded by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()

    // Obtener lugares recomendados
    LaunchedEffect(Unit) {
        if (!emailUsuario.isNullOrBlank()) {
            viewModel.obtenerLugaresVolley(context, emailUsuario)
        } else {
            Log.e("MAP_ERROR", "Email del usuario es nulo o vacío")

        }
    }

    val recommendedSites by viewModel.sitios.collectAsState()

    // Manejo de ubicación
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            try {
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    viewModel.procesarUbicacion(it.latitude, it.longitude, context)

                    if (isMapLoaded) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(
                                userLocation!!,
                                14f
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al obtener ubicación", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    // Configuración del panel deslizable
    val maxHeightDp = 400.dp
    val minHeightDp = 48.dp
    val density = LocalContext.current.resources.displayMetrics.density
    val maxHeightPx = maxHeightDp.value * density
    val minHeightPx = minHeightDp.value * density
    val maxOffset = maxHeightPx - minHeightPx
    val slideOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                    icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate("map") { popUpTo("map") { inclusive = true } } },
                    icon = { Icon(Icons.Rounded.Map, contentDescription = "Mapa") },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("sites") { popUpTo("sites") { inclusive = true } } },
                    icon = { Icon(Icons.Rounded.Store, contentDescription = "Sites") },
                    label = { Text("Mis Sites") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermission.status.isGranted,
                    mapStyleOptions = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark) else null
                ),
                onMapLoaded = {
                    isMapLoaded = true
                    userLocation?.let {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 14f))
                    }
                }
            ) {
                userLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Tu ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                recommendedSites.forEach { site ->
                    if (site.coords.size >= 2) {
                        Marker(
                            state = rememberMarkerState(position = LatLng(site.coords[0], site.coords[1])),
                            title = site.nombre,
                            snippet = site.descripcion,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                when (site.tipo.lowercase()) {
                                    "naturaleza" -> BitmapDescriptorFactory.HUE_GREEN
                                    "cultura" -> BitmapDescriptorFactory.HUE_ORANGE
                                    "gastronomia" -> BitmapDescriptorFactory.HUE_RED
                                    "aventura" -> BitmapDescriptorFactory.HUE_BLUE
                                    else -> BitmapDescriptorFactory.HUE_VIOLET
                                }
                            )
                        )
                    }
                }
            }

            // Card superior
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.newsites),
                        contentDescription = "Logo",
                        modifier = Modifier.height(40.dp)
                    )
                    Text(
                        text = "SP: 1000",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = 20.sp,
                        color = Color(0xFFE53935)
                    )
                }
            }

            // Panel deslizable inferior - MEJORADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeightDp)
                    .offset { IntOffset(x = 0, y = slideOffset.value.toInt()) }
                    .background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .align(Alignment.BottomCenter)
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            val newValue = (slideOffset.value + delta).coerceIn(0f, maxOffset)
                            coroutineScope.launch {
                                slideOffset.snapTo(newValue)
                            }
                        },
                        onDragStopped = {
                            coroutineScope.launch {
                                if (slideOffset.value > maxOffset / 2) {
                                    slideOffset.animateTo(maxOffset, tween(300))
                                    isExpanded = false
                                } else {
                                    slideOffset.animateTo(0f, tween(300))
                                    isExpanded = true
                                }
                            }
                        }
                    )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(minHeightDp)
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(Color.Gray, RoundedCornerShape(2.dp))
                        )
                    }

                    if (isExpanded) {
                        when {
                            recommendedSites.isEmpty() -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay lugares recomendados disponibles",
                                        color = Color.Gray
                                    )
                                }
                            }
                            else -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(recommendedSites) { site ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(160.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                // Contenido de información (si no hay imagen ocupa todo el espacio)
                                                Column(
                                                    modifier = Modifier
                                                        .weight(if (site.img.isNullOrEmpty()) 1f else 2f) // Ajusta el peso según si hay imagen
                                                        .fillMaxHeight()
                                                        .padding(12.dp),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = site.nombre,
                                                        fontSize = 18.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    Text(
                                                        text = site.descripcion,
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        maxLines = 3,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        FilterChip(
                                                            selected = true,
                                                            onClick = {},
                                                            label = { Text(site.tipo) },
                                                            modifier = Modifier.padding(end = 4.dp)
                                                        )

                                                        if (site.Probabilidad > 0) {
                                                            Text(
                                                                text = "${(site.Probabilidad * 100).toInt()}% match",
                                                                fontSize = 12.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                }

                                                // Mostrar imagen SOLO si está disponible
                                                if (!site.img.isNullOrEmpty()) {
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .fillMaxHeight()
                                                    ) {
                                                        AsyncImage(
                                                            model = site.img,
                                                            contentDescription = "Imagen de ${site.nombre}",
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)),
                                                            contentScale = ContentScale.Crop
                                                        )

                                                        if (site.Visitado) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .align(Alignment.TopEnd)
                                                                    .padding(8.dp)
                                                                    .background(
                                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                                                        shape = CircleShape
                                                                    )
                                                                    .padding(4.dp)
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Check,
                                                                    contentDescription = "Visitado",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


