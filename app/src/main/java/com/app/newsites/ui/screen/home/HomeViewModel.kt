package com.app.newsites.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.data.DataStoreClass
import com.app.newsites.data.SessionManager
import com.app.newsites.data.repository.UserHistoryRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val repository = UserHistoryRepository()

    private val db = FirebaseFirestore.getInstance()
    
    private val _usuario = MutableStateFlow<Map<String, Any?>>(emptyMap())
    val usuario: StateFlow<Map<String, Any?>> = _usuario

    val _userHistory = MutableStateFlow<Map<String, List<Map<String, String>>>>(emptyMap())
    val userHistory: StateFlow<Map<String, List<Map<String, String>>>> = _userHistory
    var userHistoryIds: Map<String, List<Triple<String, Int, Int>>> = emptyMap()

    fun inicializarUsuario(userId: String, context: Context) {
        viewModelScope.launch {
            val prefs = DataStoreClass(context)
            // Luego cargamos el usuario
            cargarUsuario(userId)
        }
    }

    fun cargarUsuario(userId: String) {
        viewModelScope.launch {
            repository.getUserData(userId).collect { doc ->
                if (doc != null) {
                    _usuario.value = mapOf(
                        "email" to userId,
                        "username" to doc["username"].toString(),
                        "phone" to doc["phone"].toString(),
                        "points" to doc["points"].toString(),
                        "totalSites" to doc["totalSites"].toString(),
                        "daySites" to doc["daySites"].toString(),
                        "newSites" to doc["newSites"].toString(),
                        "history" to doc["history"],
                    )

                    userHistoryIds = repository.getUserHistoryGrouped(userId)
                    SessionManager.userHistory = userHistoryIds

                    if (userHistoryIds.isNotEmpty()) {
                        cargarHistorialUsuario()
                    }
                }
            }

        }
        Log.d("USER LOG HOME", userId)
    }


    fun cargarHistorialUsuario() {
        viewModelScope.launch {
            val historyByDate = mutableMapOf<String, List<Map<String, String>>>()

            userHistoryIds.forEach { (date, sites) ->
                val siteIds = sites.map { it.first }
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("sites")
                    .whereIn(FieldPath.documentId(), siteIds)
                    .get()
                    .await()

                val sitiosPorId = snapshot.documents.associateBy { it.id }

                val listaOrdenada = sites.mapNotNull { (siteId , rate , index) ->
                    val doc = sitiosPorId[siteId]
                    if (doc != null) {
                        val nombre = doc.getString("nombre")
                        val direccion = doc.getString("direccion")
                        val descripcion = doc.getString("descripcion")
                        val img = doc.getString("img")

                        if (nombre != null && direccion != null && descripcion != null && img != null) {
                            mapOf(
                                "id" to doc.id,
                                "nombre" to nombre,
                                "direccion" to direccion,
                                "descripcion" to descripcion,
                                "img" to img,
                                "rate" to rate.toString(),
                                "index" to index.toString()
                            )
                        } else null
                    } else null
                }

                historyByDate[date] = listaOrdenada
            }

            _userHistory.value = historyByDate.toMap()
        }

    }

    fun sendRate(siteId: String, historyIndex: Int, rate: Double, userId: String) {
        Log.d("RATING", "$siteId, $historyIndex, $rate, $userId")
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("usuarios").document(userId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val history = document.get("history") as? MutableList<Map<String, Any>>
                Log.d("RATING", history.toString())
                if (history != null && history.size >= 1) {
                    // Clona el map en la posición 1 y actualiza el "rate"
                    val updatedItem = history[historyIndex].toMutableMap()
                    updatedItem["rate"] = rate

                    // Reemplaza el elemento en la posición 1
                    history[historyIndex] = updatedItem

                    // Actualiza el campo history completo
                    docRef.update("history", history)
                        .addOnSuccessListener {
                            Log.d("FIRESTORE", "Campo history actualizado correctamente")
                            db.collection("sites")
                                .document(siteId)
                                .update(
                                    mapOf(
                                        "visitasCalificadas" to FieldValue.increment(1),
                                        "totalRate" to FieldValue.increment(rate),
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Elemento agregado correctamente")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error al agregar elemento", e)
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FIRESTORE", "Error actualizando history", e)
                        }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("FIRESTORE", "Error obteniendo documento", exception)
        }
    }

}
