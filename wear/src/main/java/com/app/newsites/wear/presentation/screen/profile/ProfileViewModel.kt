package com.app.newsites.wear.presentation.screen.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.wear.presentation.data.repository.UserHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserHistoryRepository()

    private val _usuario = MutableStateFlow<Map<String, Any?>>(emptyMap())
    val usuario: StateFlow<Map<String, Any?>> = _usuario

    fun inicializarUsuario(userId: String, context: Context) {
        viewModelScope.launch {
            //val prefs = DataStoreClass(context)
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
                    // "favoritos" to doc.get("favoritos"),
                )
            }

        }
        Log.d("USER LOG HOME", userId)
    }
}