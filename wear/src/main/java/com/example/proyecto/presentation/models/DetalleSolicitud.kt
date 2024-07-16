package com.example.proyecto.models

data class DetalleSolicitud(
    val idDetalleSolicitud: Int,
    val fechaInicio: String,
    val fechaFin: String?,
    val idUsuario: Int,
    val nombreUsuario: String,
    val estatus: Boolean,
    val numeroPaso: Int
)
