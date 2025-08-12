package com.app.newsites.wear.presentation.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreClass {
    // Creación del DataStore y sus variables
    private val CURRENT_USER = stringPreferencesKey("current_user")

    // Se inicializa el DataStore
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    suspend fun setUser(context: Context, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_USER] = userId
        }
    }

    fun getUserName(context: Context): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[CURRENT_USER] ?: "dalon0904@gmail.com"
            }
    }

}

