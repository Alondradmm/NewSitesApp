package com.app.newsites.ui.session

import androidx.lifecycle.ViewModel
import com.app.newsites.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionViewModel : ViewModel() {
    private val _userId = MutableStateFlow(SessionManager.userMailId)
    val userId: StateFlow<String?> = _userId

    fun setUserId(id: String) {
        _userId.value = id
        SessionManager.userMailId = id
    }
}
