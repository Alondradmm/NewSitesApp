package com.app.newsites.wear.presentation.screen.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.newsites.wear.presentation.data.DataStoreClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(

    viewModel: ProfileViewModel = viewModel(),
    onBack: ()-> Unit
) {
    val user = viewModel.usuario.collectAsState()
    val context = LocalContext.current
    val currentUser by DataStoreClass.getUserName(context).collectAsState(initial = "")
    LaunchedEffect(currentUser) {
        Log.d("DATASTORE", currentUser)
        viewModel.inicializarUsuario(userId = currentUser, context)
    }

    Box(
        modifier = Modifier.background(Color(0xFF272727))
    ){
        LazyVerticalGrid (
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Fixed(1)
        ){
            item{
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AccountCircle,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "Usuario",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["username"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }

            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "Site Points",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["points"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }

            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "Total Sites",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["totalSites"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }

            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Permite que los hijos (Box) se estiren en altura
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "Total Sites",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["totalSites"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Permite que los hijos (Box) se estiren en altura
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddLocationAlt,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "New Sites",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["newSites"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), // Ajusta altura al contenido
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min) // Permite que los hijos (Box) se estiren en altura
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF222222))
                                .width(60.dp)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarToday,
                                contentDescription = "Logo",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFEF2C3A)
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Text(
                                text = "Day Sites",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.value["daySites"].toString(),
                                fontSize = 14.sp,
                                color = Color.White,
                            )
                        }
                    }
                }

            }

        }

        FloatingActionButton(
            onClick = onBack,
            containerColor = Color(0xFFEF2C3A),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .size(48.dp)
                .padding(4.dp)
        ) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Map", tint = Color.White)
        }
    }



}

