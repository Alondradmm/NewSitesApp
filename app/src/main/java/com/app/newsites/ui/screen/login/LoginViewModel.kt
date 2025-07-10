package com.app.newsites.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.newsites.data.repository.AuthRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoginSuccessful by mutableStateOf(false)

    fun login() {
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
                isLoginSuccessful = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }
}

