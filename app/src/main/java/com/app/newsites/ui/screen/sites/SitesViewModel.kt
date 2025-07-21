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
                        val id = doc.id
                        val nombre = doc.getString("nombre")
                        val ubicacion = doc.get("direccion")?.toString()
                        val descripcion = doc.getString("descripcion")
                        val img = doc.getString("img")

                        if (nombre != null && ubicacion != null && descripcion != null && img != null ) {
                            mapOf(
                                "id" to id,
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
    fun agregarSite(nombre: String, ubicacion: String, descripcion: String, img: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val nuevoSite = hashMapOf(
            "nombre" to nombre,
            "direccion" to ubicacion,
            "descripcion" to descripcion,
            "img" to img
        )

        db.collection("sites")
            .add(nuevoSite)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun eliminarSite(id: String, onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
        db.collection("sites")
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun editarSite(
        id: String,
        nombre: String,
        ubicacion: String,
        descripcion: String,
        img: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val nuevosDatos = mapOf(
            "nombre" to nombre,
            "direccion" to ubicacion,
            "descripcion" to descripcion,
            "img" to img
        )

        db.collection("sites")
            .document(id)
            .update(nuevosDatos)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

}
