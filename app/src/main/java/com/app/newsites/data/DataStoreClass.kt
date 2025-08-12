package com.app.newsites.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class  DataStoreClass(private val context: Context) {
    // Creación del DataStore y sus variables
    companion object {
        private val LAST_LOGIN = stringPreferencesKey("last_login")
        private val CURRENT_USER = stringPreferencesKey("current_user")

        // Se inicializa el DataStore
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }

    val lastLogin: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[LAST_LOGIN] ?: "" }

    val currentUser: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[CURRENT_USER] ?: "" }


    // Métodos para modificar los valores almacenados en el DataStore
    suspend fun setLastLogin(date: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_LOGIN] = date
        }
    }

    suspend fun setUser(userId: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_USER] = userId
        }
    }

    fun getUserName(context: Context): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[CURRENT_USER] ?: ""
            }
    }

}

