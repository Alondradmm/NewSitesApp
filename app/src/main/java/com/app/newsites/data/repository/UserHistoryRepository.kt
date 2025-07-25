package com.app.newsites.data.repository

import android.util.Log
import com.app.newsites.data.SessionManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

class UserHistoryRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserData(userId: String): Map<String, Any?>? {
        return try {
            val doc = db.collection("usuarios").document(userId).get().await()
            if (doc.exists()) {
                doc.data
            } else null
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user data", e)
            null
        }
    }

    suspend fun getUserHistoryGrouped(userId: String): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("es"))

        val doc = db.collection("usuarios").document(userId).get().await()
        val history = doc.get("history") as? List<*>

        history?.mapNotNull { item ->
            val map = item as? Map<*, *>
            val timestamp = map?.get("date") as? Timestamp
            val siteId = map?.get("site")?.toString()
            if (timestamp != null && siteId != null) {
                timestamp to siteId
            } else null
        }?.sortedByDescending { it.first }
            ?.groupBy(
                keySelector = { dateFormat.format(it.first.toDate()) },
                valueTransform = { it.second }
            )?.let {
                result.putAll(it)
            }

        SessionManager.userHistory = result
        return result
    }

}
