package com.app.newsites.ui.screen.map

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.toolbox.StringRequest
import com.app.newsites.data.DataStoreClass
import com.app.newsites.data.SessionManager
import com.app.newsites.data.repository.UserHistoryRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale


class MapViewModel : ViewModel() {

    private val repository = UserHistoryRepository()

    private val db = FirebaseFirestore.getInstance()

    private val _sites = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    private val _sitesCercanos = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val sites: StateFlow<List<Map<String, Any>>> = _sites
    val sitesCercanos: StateFlow<List<Map<String, Any>>> = _sitesCercanos


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
                        val coords = doc.getGeoPoint("coords")

                        if (nombre != null && coords != null) {
                            mapOf(
                                "id" to id,
                                "nombre" to nombre,
                                "coords" to coords,
                            )
                        } else {
                            null
                        }
                    }
                    _sites.value = lista
                }
            }
    }

    private val _usuario = MutableStateFlow<Map<String, String>>(emptyMap())
    val usuario: StateFlow<Map<String, String>> = _usuario

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
                    )

                }
            }

        }
        Log.d("USER LOG HOME", userId)
    }

    private var distanciaCercano = 10.0f // Tienes que estar a 10mts del sitio para que cuente
    private var siteMasCercano: Map<String, Any>? = null
    private var timeSiteDetected: Long = System.currentTimeMillis()
    private val timeToPointSite = 0.5 * 60 * 1000L // Tiempo que debe estar en el sitio → 1 * 60 * 1000L = 1min
    private var ultimoSiteDetectado: Map<String, Any>? = null
    private val formatDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("es"))
    private val currentDay = formatDate.format(System.currentTimeMillis())
    private var sitePointedToday: List<String>? = null

    val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    val _showProgress = MutableStateFlow(true)
    val showProgress: StateFlow<Boolean> = _showProgress

    suspend fun procesarUbicacion(latitud: Double, longitud: Double, context: Context) {
        val prefs = DataStoreClass(context)
        val userId = prefs.currentUser.first()
        viewModelScope.launch {
            SessionManager.userHistory = repository.getUserHistoryGrouped(userId)
            sitePointedToday = SessionManager.userHistory[currentDay.toString()]?.map { it.first }

            Log.d("DATASTORE_MAP", userId)
            // Obtiene la lista de sites cercanos (para reducir busqueda)
            marcadoresCercanos(latitud, longitud)

            // Convertir ubicación del usuario en un Location() (para medir)
            val userLocation = Location("").apply {
                latitude = latitud
                longitude = longitud
            }

            // Por cada site en el listado
            _sitesCercanos.value.forEach { site ->
                // Se toman las coords del sitio
                val geoPoint = site["coords"] as GeoPoint
                // Se convierte las coords en un Location() (para medir)
                val markerLocation = Location("").apply {
                    latitude = geoPoint.latitude
                    longitude = geoPoint.longitude
                }

                // Calcula la distancia entre el usuario y el site
                val distancia = userLocation.distanceTo(markerLocation)

                // Si la distancia es menor a la distancia del más cercano hasta el momento
                if (distancia < distanciaCercano) {
                    // Evalua si el último site detectado es diferente al que se detectó hace 5 seg (delay(5000) en el View)
                    if (ultimoSiteDetectado == null || ultimoSiteDetectado != site) {
                        // El contador se resetea
                        timeSiteDetected = System.currentTimeMillis()
                        // Y el último sitio se cambia al actual
                        ultimoSiteDetectado = site
                        Log.d("Mapa", "Nuevo sitio detectado, reiniciando contador")
                        Toast.makeText(context, "Site Detectado", Toast.LENGTH_SHORT).show()
                    }

                    // Establece cuál es el site más cercano y la distancia a la que se encuentra
                    siteMasCercano = site
                    distanciaCercano = distancia
                }
            }

            siteMasCercano?.let {
                val currentTime = System.currentTimeMillis()
                Log.d("HISTORIAL", sitePointedToday.toString())
                Log.d("Mapa Site Cercano", "${it["nombre"]} es el Site más cercano")
                if (sitePointedToday?.contains(it["id"]) == true) {
                    Log.d("Mapa", "Ya estuviste en este Site hoy")
                    timeSiteDetected = currentTime
                    siteMasCercano = null
                    distanciaCercano = 10.0f
                    _showProgress.value = false
                } else {
                    _showProgress.value = true
                    if (currentTime - timeSiteDetected >= timeToPointSite) {
                        timeSiteDetected = currentTime
                        Log.d("Mapa", "Estuviste 1 min en el mismo sitio")
                        _progress.value = 1f
                        siteMasCercano = null
                        distanciaCercano = 10.0f
                        _showProgress.value = false
                        Toast.makeText(context, "Site Puntuado!", Toast.LENGTH_SHORT).show()

                        val newHistory = mapOf(
                            "date" to Timestamp.now(),
                            "site" to it["id"]
                        )

                        var sitePoints: Long
                        db.collection("sites")
                            .document(it["id"].toString())
                            .get()
                            .addOnSuccessListener { doc ->
                                sitePoints = 100 + (doc.getLong("totalRate") ?: 0)/(doc.getLong("visitasCalificadas") ?: 0)*100
                                val allSites = SessionManager.userHistory.values.flatten().map { it.first }
                                val isNewSite = if (allSites.contains(it["id"].toString())) 0L else 1L
                                Log.d("NEW SITE", "El sitio es nuevo? $isNewSite")
                                db.collection("usuarios")
                                    .document(userId)
                                    .update(
                                        mapOf(
                                            "totalSites" to FieldValue.increment(1),
                                            "daySites" to FieldValue.increment(1),
                                            "points" to FieldValue.increment(sitePoints),
                                            "history" to FieldValue.arrayUnion(newHistory),
                                            "newSites" to FieldValue.increment(isNewSite)
                                        )
                                    )
                                    .addOnSuccessListener {
                                        Log.d("Firestore", "Elemento agregado correctamente")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Firestore", "Error al agregar elemento", e)
                                    }
                            }
                    } else {
                        _progress.value = ((currentTime - timeSiteDetected)/timeToPointSite).toFloat()
                        Log.d("Mapa", progress.value.toString())
                    }
                }
            }
        }

    }

    private fun marcadoresCercanos(userLat: Double, userLng: Double, rangoMetros: Double = 300.0) {
        val earthRadius = 6371000.0

        val deltaLat = rangoMetros / earthRadius * (180 / Math.PI)
        val deltaLng = rangoMetros / (earthRadius * kotlin.math.cos(Math.toRadians(userLat))) * (180 / Math.PI)

        val filtrados = _sites.value.filter { site ->
            val geo = site["coords"] as? GeoPoint ?: return@filter false
            geo.latitude in (userLat - deltaLat)..(userLat + deltaLat) &&
                    geo.longitude in (userLng - deltaLng)..(userLng + deltaLng)
        }

        _sitesCercanos.value = filtrados
    }

    private val _sitios = MutableStateFlow<List<SiteResponse>>(emptyList())
    val sitios: StateFlow<List<SiteResponse>> = _sitios

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun obtenerLugaresVolley(context: Context, email: String) {
        _loading.value = true
        _errorMessage.value = null

        Log.d("MAP_VIEWMODEL", "Obteniendo lugares para: $email")

        val encodedEmail = URLEncoder.encode(email, "UTF-8")
        val url = "http://192.168.0.6" + ":5000/recomendar/lugares?email=$encodedEmail"

        Log.d("API_REQUEST", "URL: $url")

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("API_RESPONSE", "Respuesta recibida: ${response.take(200)}...")
                try {
                    val listaSites = parseSitesFromJson(response)
                    Log.d("API_SUCCESS", "Lugares obtenidos: ${listaSites.size}")
                    _sitios.value = listaSites
                } catch (e: Exception) {
                    Log.e("API_PARSE_ERROR", "Error al parsear: ${e.message}")
                    _errorMessage.value = "Error al procesar los datos"
                }
                _loading.value = false
            },
            { error ->
                Log.e("API_ERROR", "Error: ${error.message}")
                _errorMessage.value = when {
                    error.networkResponse?.statusCode == 404 -> "Servicio no disponible"
                    error is TimeoutError -> "Tiempo de espera agotado"
                    else -> "Error de conexión"
                }
                _loading.value = false
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    private fun parseSitesFromJson(jsonStr: String): List<SiteResponse> {
        val list = mutableListOf<SiteResponse>()
        try {
            Log.d("API_RESPONSE", "Respuesta JSON: $jsonStr")

            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                // Coordenadas (asegurar que siempre sean 2 valores)
                val coords = mutableListOf<Double>()
                if (obj.has("coords")) {
                    val coordsJson = obj.getJSONArray("coords")
                    for (j in 0 until coordsJson.length()) {
                        coords.add(coordsJson.getDouble(j))
                    }
                }
                if (coords.size < 2) {
                    coords.add(0.0)
                    coords.add(0.0)
                }

                list.add(
                    SiteResponse(
                        nombre = obj.getString("nombre"),
                        descripcion = obj.getString("descripcion"),
                        tipo = obj.getString("tipo"),
                        Probabilidad = obj.getDouble("Probabilidad"),
                        Visitado = obj.getBoolean("Visitado"),
                        coords = coords,
                        direccion = obj.optString("direccion", ""),
                        img = obj.optString("img", ""),
                        owner = obj.optString("owner", ""),
                        valoracion = obj.optString("valoracion", "0")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("JSON_PARSE_ERROR", "Error al parsear JSON: ${e.message}")
            e.printStackTrace()
        }
        return list
    }
}
