package com.app.newsites.data

object SessionManager {
    var userId: String? = null
    var userHistory: Map<String, List<String>> = emptyMap()
}