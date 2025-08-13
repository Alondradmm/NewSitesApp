package com.app.newsites.ui.screen.sites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SitesScreen(
    navController: NavController,
    viewModel: SitesViewModel = viewModel()
) {
    val user = viewModel.usuario.collectAsState()//
    val sitesState = viewModel.sites.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val userEmail = user.value["email"]?.toString() ?: ""//


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Sites", color = Color.White) },
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
                            Icons.Default.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate("map/$userEmail") {
                            popUpTo("map") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        navController.navigate("sites") {
                            popUpTo("sites") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Store,
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
                            Icons.Default.Person,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Perfil") }
                )
            }
        },
        //cambiar a la pestaña de agregar
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate("agregar_site") },
                containerColor = Color(0xFFE53935)
            ) {
                Icon(Icons.Rounded.AddLocationAlt, contentDescription = "Agregar", tint = Color.White)
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
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .background(
                            when (site["tipo"]?.lowercase()) {
                                "naturaleza" -> Color(0xFF8BC34A)
                                "cultura" -> Color(0xFFFF9800)
                                "gastronomia" -> Color(0xFFF44336)
                                "aventura" -> Color(0xFF2196F3)
                                else -> Color.Red
                            }
                        )
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Category, contentDescription = "Tipo", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(site["tipo"] ?: "Tipo no especificado", style = MaterialTheme.typography.bodySmall)
                    }
                    //tipo

                }
            }
        }
    }
}
