package com.app.newsites.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RegisterRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun registerUser(email: String, password: String, phone: String, user: String): Result<Unit> = runCatching {
        try {
            val userAuthenticated = auth.createUserWithEmailAndPassword(email, password).await()
            if (userAuthenticated.user == null) {
                throw Exception("Se ha producido un error al autenticar usuario")
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("Contraseña incorrecta")
        }

        db.collection("usuarios").document(email).set(
            hashMapOf(
                "username" to user,
                "phone" to phone
            )
        )
    }
}
