package com.app.newsites.data.repository

import android.util.Log
import com.app.newsites.data.SessionManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class UserHistoryRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getUserData(userId: String): Flow<Map<String, Any?>?> = callbackFlow {
        val listenerRegistration = db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.data)
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }


    suspend fun getUserHistoryGrouped(userId: String): Map<String, List<Triple<String, Int, Int>>> {
        val result = mutableMapOf<String, List<Triple<String, Int, Int>>>()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("es"))

        val doc = db.collection("usuarios").document(userId).get().await()
        val history = doc.get("history") as? List<*>

        // mapIndexed para obtener índice también
        val indexedList = history?.mapIndexedNotNull { index, item ->
            val map = item as? Map<*, *>
            val timestamp = map?.get("date") as? Timestamp
            val siteId = map?.get("site")?.toString()
            val rateInt = (map?.get("rate") as? Number)?.toInt() ?: 0
            if (timestamp != null && siteId != null) {
                Triple(timestamp, siteId, rateInt) to index
            } else null
        } ?: emptyList()

        // Ordenar por timestamp descendente
        val sortedList = indexedList.sortedByDescending { it.first.first }

        // Agrupar por fecha con Triple(siteId, rate, index)
        val grouped = sortedList.groupBy(
            keySelector = { dateFormat.format(it.first.first.toDate()) },
            valueTransform = {
                Triple(it.first.second, it.first.third, it.second)
            }
        )

        result.putAll(grouped)
        Log.d("RESULT", result.toString())
        SessionManager.userHistory = result
        return result
    }

}
