package com.app.newsites.ui.screen.home

import androidx.lifecycle.ViewModel
import com.app.newsites.data.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun cargarUsuario(userId: String) {
        db.collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener { documento ->
                val usuario = documento.toObject(Usuario::class.java)
                _usuario.value = usuario
            }
            .addOnFailureListener {
                _usuario.value = null
            }
    }
}
