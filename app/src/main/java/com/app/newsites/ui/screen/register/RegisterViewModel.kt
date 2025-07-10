package com.app.newsites.ui.screen.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.newsites.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepository()

    var user by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isRegisterSuccessful by mutableStateOf(false)

    fun register() {

        if (email.isBlank() || user.isBlank() || phone.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, llena todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val result = repository.registerUser(user, email, phone, password)

            isLoading = false

            if (result.isSuccess) {
                isRegisterSuccessful = true
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }

    }
}