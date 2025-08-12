package com.app.newsites.ui.screen.map

data class SiteResponse(
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val Probabilidad: Double,
    val Visitado: Boolean,
    val coords: List<Double>,
    val direccion: String = "",
    val img: String = "",
    val owner: String = "",
    val valoracion: String = ""
)