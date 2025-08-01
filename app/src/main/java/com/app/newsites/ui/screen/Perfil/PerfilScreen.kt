package com.app.newsites.ui.screen.Perfil

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.ui.screen.map.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        }
        ,
        bottomBar = {
            NavigationBar (windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = true,
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
                        navController.navigate("map") {
                            popUpTo("map") { inclusive = true }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Home"
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
    ){
        paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ){}
    }
}
