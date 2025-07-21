package com.app.newsites.ui.screen.sites

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarSiteScreen(
    navController: NavController,
    viewModel: SitesViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var img by remember { mutableStateOf("") }

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
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación") },
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
            Button(
                onClick = {
                    if (nombre.isNotBlank() && ubicacion.isNotBlank() && descripcion.isNotBlank() && img.isNotBlank()) {
                        viewModel.agregarSite(
                            nombre = nombre,
                            ubicacion = ubicacion,
                            descripcion = descripcion,
                            img = img,
                            onSuccess = {
                                navController.popBackStack()
                            },
                            onFailure = { exception ->
                                val context = null
                                Toast.makeText(context, "Error al guardar el sitio", Toast.LENGTH_SHORT).show()

                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar", color = Color.White)
            }
        }
    }
}
