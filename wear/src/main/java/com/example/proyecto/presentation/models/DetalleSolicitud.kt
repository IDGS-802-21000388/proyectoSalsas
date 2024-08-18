package com.example.proyecto.models


//Modelo de datos para la solicitud
data class DetalleSolicitud(
    val idDetalleSolicitud: Int,
    var idSolicitud: Int,
    val fechaInicio: String,
    val fechaFin: String?,
    val idUsuario: Int,
    val nombreUsuario: String,
    val estatus: Boolean,
    val numeroPaso: Int
)
