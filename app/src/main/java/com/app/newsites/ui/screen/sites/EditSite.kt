package com.app.newsites.ui.screen.sites

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.data.DataStoreClass
import com.app.newsites.ui.screen.home.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlin.collections.set
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditSite(
    siteId: String,
    navController: NavController,
    db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    viewModel: HomeViewModel  = viewModel()

){
    val user = viewModel.usuario.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    val userEmail = user.value["email"]?.toString() ?: ""
    val tipos = listOf("Naturaleza", "Cultura", "Gastronomía", "Aventura")
    var tipoSeleccionado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Modificando: ${if (nombre.isNotBlank()) nombre else "..."}", color = Color.White)
                },
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
        }
    ) { innerPadding ->

        // Carga inicial
        LaunchedEffect(siteId) {
            val doc = db.collection("sites").document(siteId).get().await()
            nombre = doc.getString("nombre") ?: ""
            descripcion = doc.getString("descripcion") ?: ""
            tipoSeleccionado = doc.getString("tipo") ?: ""
        }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
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
                Spacer(modifier = Modifier.height(16.dp))

                Text("Tipo de sitio:")

                tipos.forEach { tipo ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo }
                        )
                        Text(tipo)
                    }
                }

                Button(onClick = {
                    db.collection("sites").document(siteId)
                        .update(
                            mapOf(
                                "nombre" to nombre,
                                "descripcion" to descripcion,
                                "tipo" to tipoSeleccionado
                            )
                        )
                        .addOnSuccessListener {
                            navController.popBackStack()
                        }
                },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                    Text("Guardar cambios",color = Color.White)
                }
            }
    }
}

