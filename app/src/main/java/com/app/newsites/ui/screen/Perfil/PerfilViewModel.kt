package com.app.newsites.ui.screen.Perfil

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _usuario = MutableStateFlow<Map<String, Any?>>(emptyMap())
    val usuario: StateFlow<Map<String, Any?>> = _usuario


}