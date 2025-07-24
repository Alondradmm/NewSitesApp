package com.app.newsites.data.repository

import com.app.newsites.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val userEmailExist = db.collection("usuarios").document(email).get().await().exists()
        if (userEmailExist) {
            try {
                val userAuthenticated = auth.signInWithEmailAndPassword(email, password).await()
                if (userAuthenticated.user == null) {
                    throw Exception("Se ha producido un error al autenticar usuario")
                }
                SessionManager.userId = email
            } catch (e : FirebaseAuthInvalidCredentialsException) {
                throw Exception("Contraseña incorrecta")
            }
        } else {
            throw Exception("El correo ${email} no está registrado")
        }
    }
}