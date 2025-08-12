package com.app.newsites.wear.presentation.screen.sync

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.newsites.wear.R
import com.app.newsites.wear.presentation.data.DataStoreClass
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

@Composable
fun WelcomeScreen(
    onNavigate: ()-> Unit
) {
    val context = LocalContext.current
    val currentUser by DataStoreClass.getUserName(context).collectAsState(initial = "")
    LaunchedEffect(currentUser) {
        Log.d("DATASTORE", currentUser)

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)),
    ){
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 10.dp,
                alignment = Alignment.CenterVertically
            )
        ){
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Logo",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFFE53935)
                )
                Text(
                    text = "New Sites",
                    color = Color(0xFFE53935),
                    fontFamily = FontFamily(Font(R.font.alata)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }

            Text(
                text = "Iniciar como",
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.alata)),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = currentUser,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.alata))
            )
            Button(
                onClick =
                    {
                        isInternetAvailable { available ->
                            if (available) {
                                onNavigate()
                            } else {
                                Toast.makeText(context, "No hay conexión a internet", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                ,
                modifier = Modifier,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                ),
            ) {
                Text(
                    text = "Acceder",
                    color = Color.White
                )
            }
        }

    }
}

fun isInternetAvailable(onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("usuarios").document()
        .get(Source.SERVER) // fuerza ir al servidor
        .addOnSuccessListener {
            onResult(true) // sí hay conexión y Firestore responde
        }
        .addOnFailureListener {
            onResult(false) // no hay conexión o Firestore está caído
        }
}