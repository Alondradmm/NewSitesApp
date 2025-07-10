package com.app.newsites.ui.screen.sites

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SitesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _sites = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val sites: StateFlow<List<Map<String, String>>> = _sites

    init {
        obtenerSites()
    }

    private fun obtenerSites() {
        db.collection("sites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val nombre = doc.getString("nombre")
                        val ubicacion = doc.get("direccion")?.toString()

                        val descripcion = doc.getString("descripcion")
                        val img = doc.getString("img")
                        if (nombre != null && ubicacion != null && descripcion != null && img != null ) {
                            mapOf(
                                "nombre" to nombre,
                                "ubicacion" to ubicacion,
                                "descripcion" to descripcion,
                                "img" to img
                            )
                        } else {
                            null
                        }
                    }
                    _sites.value = lista
                }
            }
    }
}
