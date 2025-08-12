package com.app.newsites.wear.presentation
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.app.newsites.wear.presentation.data.DataStoreClass
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearDataLifecycleService : LifecycleService(), DataClient.OnDataChangedListener {

    override fun onCreate() {
        super.onCreate()
        Log.d("WearDataService", "Service creado y listener agregado")
        Wearable.getDataClient(this).addListener(this)

        // Para verificar dataItems almacenados:
        Wearable.getDataClient(this).dataItems
            .addOnSuccessListener { dataItemBuffer ->
                Log.d("WearDataService", "DataItems en Data Layer:")
                for (dataItem in dataItemBuffer) {
                    Log.d("WearDataService", " - Path: ${dataItem.uri.path}")
                }
                dataItemBuffer.release()
            }
            .addOnFailureListener {
                Log.e("WearDataService", "Error obteniendo dataItems: $it")
            }
    }

    override fun onDestroy() {
        Wearable.getDataClient(this).removeListener(this)
        Log.d("com.app.newsites.wear.presentation.WearDataLifecycleService", "Listener removido")
        super.onDestroy()
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("WearDataService", "onDataChanged invocado")
        for (event in dataEvents) {
            val path = event.dataItem.uri.path ?: "null"
            Log.d("WearDataService", "Evento en path: $path")
            if (event.type == DataEvent.TYPE_CHANGED && path == "/user") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                Log.d("DATA_MAP", dataMap.toString())
                val userId = dataMap.getString("user")
                Log.d("WearDataService", "User ID recibido: $userId")

                userId?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        DataStoreClass.setUser(applicationContext, it)
                        Log.d("WearDataService", "User ID guardado en DataStore: $it")
                    }
                }
            }
        }
    }


}

