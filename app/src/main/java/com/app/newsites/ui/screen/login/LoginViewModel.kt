package com.app.newsites.ui.screen.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.data.DataStoreClass
import com.app.newsites.data.repository.AuthRepository
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoginSuccessful by mutableStateOf(false)

    fun login(context: Context) {
        val prefs = DataStoreClass(context)
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, llena todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = repository.login(email, password)

            isLoading = false

            if (result.isSuccess) {
                prefs.setUser(email)

                val lastLogin = prefs.lastLogin.first()
                val formatDate = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("es"))
                val currentDay = formatDate.format(System.currentTimeMillis())

                // Si es un nuevo día, reseteamos daySites
                if (lastLogin != currentDay) {
                    FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(email)
                        .update("daySites", 0)
                        .addOnSuccessListener {
                            Log.d("Firestore", "daySites reseteado")
                        }

                    prefs.setLastLogin(currentDay)
                }


                val putDataMapRequest = PutDataMapRequest.create("/user")
                val dataMap = putDataMapRequest.dataMap  // DataMap interno del request

                dataMap.putString("user", email)
                dataMap.putLong("timestamp", System.currentTimeMillis())

                val request = putDataMapRequest.asPutDataRequest().setUrgent()

                Wearable.getDataClient(context).putDataItem(request)
                    .addOnSuccessListener {
                        Log.d("WearSync", "Datos enviados correctamente")
                    }
                    .addOnFailureListener {
                        Log.e("WearSync", "Error al enviar datos", it)
                    }



                isLoginSuccessful = true



            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }
}

