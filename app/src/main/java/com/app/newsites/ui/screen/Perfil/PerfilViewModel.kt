package com.app.newsites.ui.screen.Perfil

import android.util.Log
import androidx.lifecycle.ViewModel
import com.app.newsites.data.DataStoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    //funcion para actualizar la coleccion del usuario para el formulario
    fun encuestaMejora(
        email: String,
        especial: Boolean,
        comunicacion: List<String>,
        intereses: List<String>,
        pago: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        //Es para actualizar la coleccion del usuario
        val datos = mapOf(
            "especial" to especial,
            "comunicacion" to comunicacion,
            "intereses" to intereses,
            "pago" to pago
        )

        db.collection("usuarios")
            .document(email)
            .update(datos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}