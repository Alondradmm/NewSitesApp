package com.app.newsites.wear.presentation.screen.siteDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.wear.presentation.data.repository.UserHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SiteDetailViewModel : ViewModel() {
    private val repository = UserHistoryRepository()

    private val _site = MutableStateFlow<Map<String, Any?>>(emptyMap())
    val site: StateFlow<Map<String, Any?>> = _site

    fun cargarSite(siteId: String) {
        viewModelScope.launch {
            val doc = repository.getSiteData(siteId)
            if (doc != null) {
                _site.value = mapOf(
                    "nombre" to doc["nombre"].toString(),
                    "descripcion" to doc["descripcion"].toString(),
                    "direccion" to doc["direccion"].toString(),
                    "tipo" to doc["tipo"].toString(),
                    "valoracion" to doc["valoracion"].toString(),
                )
            }

        }
    }
}