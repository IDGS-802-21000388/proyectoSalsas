package com.example.proyecto.models

data class PasoReceta(
    val idPasoReceta: Int,
    val paso: Int,
    val descripcion: String,
    val idProducto: Int
)