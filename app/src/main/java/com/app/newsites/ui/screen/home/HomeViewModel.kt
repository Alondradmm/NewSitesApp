package com.app.newsites.ui.screen.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.data.DataStoreClass
import com.app.newsites.data.SessionManager
import com.app.newsites.data.repository.UserHistoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
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
    var userHistoryIds: Map<String, List<String>> = emptyMap()

    fun inicializarUsuario(userId: String, context: Context) {
        viewModelScope.launch {
            val prefs = DataStoreClass(context)
            // Luego cargamos el usuario
            cargarUsuario(userId)
        }
    }

    fun cargarUsuario(userId: String) {
        viewModelScope.launch {
            val doc = repository.getUserData(userId)
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
                    // "favoritos" to doc.get("favoritos"),
                )

                userHistoryIds = repository.getUserHistoryGrouped(userId)
                SessionManager.userHistory = userHistoryIds

                if (userHistoryIds.isNotEmpty()) {
                    cargarHistorialUsuario()
                }
            }

        }
        Log.d("USER LOG HOME", userId)
    }


    fun cargarHistorialUsuario() {
        viewModelScope.launch {
            val historyByDate = mutableMapOf<String, List<Map<String, String>>>()

            userHistoryIds.forEach { (date, sites) ->
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("sites")
                    .whereIn(FieldPath.documentId(), sites)
                    .get()
                    .await()

                val sitiosPorId = snapshot.documents.associateBy { it.id }

                val listaOrdenada = sites.mapNotNull { siteId ->
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
                                "img" to img
                            )
                        } else null
                    } else null
                }

                historyByDate[date] = listaOrdenada
            }

            _userHistory.value = historyByDate.toMap()
        }

    }

}
