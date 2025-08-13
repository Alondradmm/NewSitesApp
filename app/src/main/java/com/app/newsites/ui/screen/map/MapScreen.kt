package com.app.newsites.ui.screen.map

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
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
            viewModel.cargarUsuario(emailUsuario)
        } else {
            Log.e("MAP_ERROR", "Email del usuario es nulo o vacío")

        }

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
                Log.e("MAP_ERROR", "No se autorizó el servicio de ubicación")
            }
            delay(5000)
        }
    }

    val recommendedSites by viewModel.sitios.collectAsState()
    val sitesState = viewModel.sites.collectAsState()


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
    val slideOffset = remember { Animatable(maxOffset) }
    val coroutineScope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }

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
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermission.status.isGranted,
                    mapStyleOptions = if (isDarkTheme) MapStyleOptions.loadRawResourceStyle(context, R.raw.map_dark) else null
                ),
                contentPadding = PaddingValues(
                    bottom = minHeightDp
                ),
                onMapLoaded = {
                    isMapLoaded = true
                    userLocation?.let {
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 14f))
                    }
                }
            ) {
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

            // Card superior

            val user = viewModel.usuario.collectAsState()
            val progress = viewModel.progress.collectAsState()
            val showProgress = viewModel.showProgress.collectAsState()
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .wrapContentWidth()
                    .padding(horizontal = 100.dp, vertical = 10.dp),
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Row(
                            modifier = Modifier
                                .height(IntrinsicSize.Min) // Permite que los hijos (Box) se estiren en altura
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.Red)
                                    .width(60.dp)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Logo",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(6.dp)
                            ) {
                                Text(
                                    text = "Site Points",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = user.value["points"].toString(),
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                )
                            }
                        }

                    }

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
                            .alpha(animatedAlpha)
                            .fillMaxWidth()
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
                                    contentPadding = PaddingValues(bottom = 12.dp),
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
                                                        .weight(if (site.img.isEmpty()) 1f else 2f) // Ajusta el peso según si hay imagen
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
                                                if (site.img.isNotEmpty()) {
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


