package com.app.newsites.ui.screen.sites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SitesScreen(
    navController: NavController? = null,
    viewModel: SitesViewModel = viewModel()
) {
    val sitesState = viewModel.sites.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Sites", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate("agregar_site") },
                containerColor = Color(0xFFE53935)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },

        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(sitesState.value) { site ->
                SiteItem(
                    site = site,
                    onEdit = {
                        // Acción editar
                        // agregar despues cuando no este muerto de sueño
                        val id = site["id"]
                        navController?.navigate("editSite/${site["id"]}")
                    },
                    onDeleteConfirmed = {
                        val id = site["id"]
                        if (id != null) {
                            viewModel.eliminarSite(
                                id,
                                onSuccess = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Sitio eliminado exitosamente")
                                    }
                                },
                                onFailure = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error al eliminar el sitio")
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SiteItem(
    site: Map<String, String>,
    onEdit: () -> Unit,
    onDeleteConfirmed: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val editAction = SwipeAction(
        icon = rememberVectorPainter(Icons.Default.Edit),
        background = Color(0xFF1976D2),
        onSwipe = { onEdit() }
    )

    val deleteAction = SwipeAction(
        icon = rememberVectorPainter(Icons.Default.Delete),
        background = Color(0xFFD32F2F),
        onSwipe = { showDialog = true }
    )

    // Diálogo de confirmación para eliminar
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar sitio") },
            text = { Text("¿Estás seguro de que quieres eliminar este sitio?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = MaterialTheme.shapes.medium
    ) {
        SwipeableActionsBox(
            startActions = listOf(deleteAction),
            endActions = listOf(editAction),
            swipeThreshold = 100.dp,
            modifier = Modifier.clip(MaterialTheme.shapes.medium)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val imagenUrl = site["img"]
                if (!imagenUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imagenUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del sitio",
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFD32F2F))
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = site["nombre"] ?: "Nombre de Site",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = "Ubicación", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(site["ubicacion"] ?: "Dirección", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Descripción", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(site["descripcion"] ?: "Descripción", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
