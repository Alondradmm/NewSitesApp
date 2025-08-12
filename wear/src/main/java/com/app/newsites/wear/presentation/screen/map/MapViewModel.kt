package com.app.newsites.wear.presentation.screen.map

import android.content.Context
import android.location.Location
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.wear.presentation.data.DataStoreClass
import com.app.newsites.wear.presentation.data.repository.UserHistoryRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MapViewModel : ViewModel() {

    private val repository = UserHistoryRepository()


    private val db = FirebaseFirestore.getInstance()

    private val _sites = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    private val _sitesCercanos = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val sites: StateFlow<List<Map<String, Any>>> = _sites

    val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    val _showProgress = MutableStateFlow(false)
    val showProgress: StateFlow<Boolean> = _showProgress


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

                        if (nombre != null && coords != null ) {
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

    private var distanciaCercano = 10.0f // Tienes que estar a 10mts del sitio para que cuente
    private var siteMasCercano: Map<String, Any>? = null
    private var timeSiteDetected: Long = System.currentTimeMillis()
    private val timeToPointSite = 0.5 * 60 * 1000L // Tiempo que debe estar en el sitio → 1 * 60 * 1000L = 1min
    private var ultimoSiteDetectado: Map<String, Any>? = null
    private val formatDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("es"))
    private val currentDay = formatDate.format(System.currentTimeMillis())
    private var sitePointedToday: List<String>? = null
    var sitesHistory:  Map<String, List<String>> = emptyMap()
    private var userId: String = ""
    fun procesarUbicacion(latitud : Double, longitud: Double, context: Context){
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        viewModelScope.launch {
            userId = DataStoreClass.getUserName(context).first()
            sitesHistory = repository.getUserHistoryGrouped(userId)
            sitePointedToday = sitesHistory[currentDay.toString()]

            Log.d("HISTORIAL", sitePointedToday.toString())
            Log.d("DATASTORE_MAP", userId)
            Log.d("USER_COORDS", "Lat: $latitud Lon: $longitud")
            // Obtiene la lista de sites cercanos (para reducir busqueda)
            marcadoresCercanos(latitud, longitud)

            // Convertir ubicación del usuario en un Location() (para medir)
            val userLocation = Location("").apply {
                latitude = latitud
                longitude = longitud
            }

            // Por cada site en el listado
            _sitesCercanos.value.forEach() { site ->
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
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    }

                    // Establece cuál es el site más cercano y la distancia a la que se encuentra
                    siteMasCercano = site
                    distanciaCercano = distancia
                }

            }


            siteMasCercano?.let{
                val currentTime = System.currentTimeMillis()
                //Log.d("HISTORIAL", sitePointedToday.toString())
                Log.d("Mapa Site Cercano", "${it["nombre"]}es el Site más cercano")
                if (sitePointedToday?.contains(it["id"]) == true){
                    Log.d("Mapa", "Ya estuviste en este Site hoy")
                    timeSiteDetected = currentTime
                    siteMasCercano = null
                    distanciaCercano = 10.0f
                    _showProgress.value = false
                }else{
                    _showProgress.value = true
                    if (currentTime - timeSiteDetected >= timeToPointSite){
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
                                sitePoints = doc.getLong("points") ?: 0
                                val allSites = sitesHistory.values.flatten() //SessionManager.userHistory.values.flatten()
                                val isNewSite = if(allSites.contains(it["id"].toString())) 0L else 1L
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

        Log.d("SITES_CERCANOS", filtrados.toString())

        _sitesCercanos.value = filtrados
    }
}