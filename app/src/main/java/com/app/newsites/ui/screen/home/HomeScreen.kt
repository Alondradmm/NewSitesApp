package com.app.newsites.ui.screen.home

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Star
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.newsites.R
import com.app.newsites.data.DataStoreClass
import kotlinx.coroutines.flow.first
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val user = viewModel.usuario.collectAsState()
    val userHistory = viewModel.userHistory.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val prefs = DataStoreClass(context)
        val currentUser = prefs.currentUser.first()
        Log.d("DATASTORE", currentUser)
        viewModel.inicializarUsuario(userId = currentUser, context)
    }
    Scaffold (
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
                //Perfil
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
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

                    Text(//Obtener usuario
                        text = user.value["username"].toString(),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyVerticalGrid (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    columns = GridCells.Fixed(2)
                ){
                    item {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(), // Ajusta altura al contenido
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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

                    }

                    item {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(), // Ajusta altura al contenido
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        imageVector = Icons.Rounded.LocationOn,
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
                                        text = "Total Sites",
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.value["totalSites"].toString(),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                    )
                                }
                            }
                        }

                    }

                    item {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(), // Ajusta altura al contenido
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        imageVector = Icons.Rounded.AddLocationAlt,
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
                                        text = "New Sites",
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.value["newSites"].toString(),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                    )
                                }
                            }
                        }

                    }

                    item {
                        Card(
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(), // Ajusta altura al contenido
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
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
                                        imageVector = Icons.Rounded.CalendarToday,
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
                                        text = "Day Sites",
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.value["daySites"].toString(),
                                        fontFamily = FontFamily(Font(R.font.alata)),
                                        fontSize = 20.sp,
                                        color = Color.Red,
                                    )
                                }
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

                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 10.dp)
                ){
                    userHistory.value.forEach { (date, history) ->
                        item {
                            Text(date)
                        }

                        if (history.isNotEmpty()){
                            items(history) { site ->
                                Card(
                                    colors = CardDefaults.cardColors(Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                )
                                {
                                    SwipeableActionsBox (
                                        startActions = listOf(delete),
                                    ){
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(IntrinsicSize.Min),
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Box (
                                                modifier = Modifier
                                                    .background(Color.Red)
                                                    .width(80.dp)
                                                    .fillMaxHeight()
                                            )
                                            Column (
                                                modifier = Modifier
                                                    .weight(7f)
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                            ){
                                                Text(
                                                    text = site["nombre"].toString(),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

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
                                                        text = site["descripcion"].toString(),
                                                        fontSize = 14.sp,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }


                            }
                        } else {
                            item {
                                Card (
                                    colors = CardDefaults.cardColors(Color.White),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(IntrinsicSize.Min),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Box (
                                            modifier = Modifier
                                                .background(Color.LightGray)
                                                .width(80.dp)
                                                .fillMaxHeight()
                                        )
                                        Column (
                                            modifier = Modifier
                                                .weight(7f)
                                                .padding(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                        ){
                                            Text(
                                                text = "Sitio desconocido",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )

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
                                                    text = "El site ya no existe",
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
        }
    }


}

