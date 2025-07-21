package com.app.newsites.ui.screen.sites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditSite(
    siteId: String,
    navController: NavController,
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
){
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    
    // Carga inicial
    LaunchedEffect(siteId) {
        db.collection("sites").document(siteId).get().addOnSuccessListener { doc ->
            nombre = doc.getString("nombre") ?: ""
            descripcion = doc.getString("descripcion") ?: ""
        }
    }

    Column(Modifier.padding(16.dp)) {
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

        Button(onClick = {
            db.collection("sites").document(siteId)
                .update(mapOf(
                    "nombre" to nombre,
                    "descripcion" to descripcion
                ))
                .addOnSuccessListener {
                    navController.popBackStack()
                }
        }) {
            Text("Guardar cambios")
        }
    }
}