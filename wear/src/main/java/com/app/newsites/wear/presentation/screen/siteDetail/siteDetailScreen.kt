package com.app.newsites.wear.presentation.screen.siteDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import com.app.newsites.wear.R

@Composable
fun SiteDetailScreen(
    viewModel: SiteDetailViewModel = viewModel(),
    onBack: () -> Unit,
    siteId: String,
) {
    val site = viewModel.site.collectAsState()

    LaunchedEffect(siteId) {
        viewModel.cargarSite(siteId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF272727))
    ){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 0.dp)
        ){
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEF2C3A))
                ){
                    Text(
                        text = site.value["nombre"].toString(),
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        fontFamily = FontFamily(Font(R.font.alata)),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ){
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = "Descripcion",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )

                    Text(
                        text = site.value["descripcion"].toString()
                    )
                }
            }

            item {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Dirección",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )

                    Text(
                        text = site.value["direccion"].toString()
                    )
                }
            }

            item {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(30.dp),
                        tint = Color.White
                    )

                    Text(
                        text = site.value["valoracion"].toString()
                    )
                }
            }

            item {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        imageVector = Icons.Rounded.Sell,
                        contentDescription = "Rating",
                        modifier = Modifier.size(30.dp).padding(horizontal = 2.dp),
                        tint = Color.White
                    )

                    Text(
                        text = site.value["tipo"].toString()
                    )
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