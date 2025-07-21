package com.app.newsites.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.R
import com.app.newsites.ui.session.SessionViewModel
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val session: SessionViewModel = viewModel()
    val userId by session.userId.collectAsState()
    val user by viewModel.usuario.collectAsState()

    LaunchedEffect(key1 = userId) {
        viewModel.cargarUsuario(userId.toString())
    }
    Scaffold (
        bottomBar = {
            NavigationBar (windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = true,
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
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
                    selected = true,
                    onClick = {
                        navController.navigate("register") {
                            popUpTo("register") { inclusive = true }
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
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(paddingValues)
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "NewSites",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "Bienvenid@",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = 20.sp,
                    )

                    Text(
                        text = user?.username ?: "",
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ){
                    Card (
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column (
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Image(
                                    painter = painterResource(id = R.drawable.newsites),
                                    contentDescription = "Logo",
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .height(60.dp),
                                )
                                Text(
                                    text = "SitePoints",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "1000",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                )
                            }

                        }
                    }

                    Card (
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column (
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ){
                                Image(
                                    painter = painterResource(id = R.drawable.newsites),
                                    contentDescription = "Logo",
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier
                                        .height(60.dp),
                                )
                                Text(
                                    text = "New Sites",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "3",
                                    fontFamily = FontFamily(Font(R.font.alata)),
                                    fontSize = 20.sp,
                                    color = Color.Red,
                                )
                            }

                        }
                    }
                }
                Text(
                    text = "Historial",
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontSize = 20.sp,
                )

                val delete = SwipeAction(
                    icon = rememberVectorPainter(Icons.Default.Delete),
                    background = Color.Red,
                    onSwipe = { }
                )

                SwipeableActionsBox (
                    startActions = listOf(delete),
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                ){
                    Card (
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(0.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Box (
                                modifier = Modifier
                                    .background(Color.Red)
                                    .width(80.dp)
                                    .fillMaxHeight()
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column (
                                modifier = Modifier.weight(7f),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ){
                                Text(
                                    text = "Nombre de Site",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ){
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Ícono",
                                        modifier = Modifier
                                            .size(10.dp)
                                            .fillMaxHeight()
                                    )

                                    Text(
                                        text = "Nombre de Site",
                                        fontSize = 14.sp,
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Ícono",
                                        modifier = Modifier
                                            .size(10.dp)
                                            .fillMaxHeight()
                                    )

                                    Text(
                                        text = "Descripción",
                                        fontSize = 14.sp,
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

