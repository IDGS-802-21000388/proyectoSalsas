package com.example.proyecto.models

//Modelo de datos para la solicitud
data class SolicitudProduccion(
    val idSolicitud: Int,
    val cantidadProduccion: Int,
    val fechaSolicitud: String?,
    val estatus: Int,
    val idProducto: Int,
    val nombreProducto: String?,
    var idUsuario: Int,
    var nombreCliente: String?,
    val detalleSolicituds: List<DetalleSolicitud>?,
    var nombreAsignado: String? = null
)