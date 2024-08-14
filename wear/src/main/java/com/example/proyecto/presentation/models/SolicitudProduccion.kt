package com.example.proyecto.models

data class SolicitudProduccion(
    val idSolicitud: Int,
    val cantidadProduccion: Int,
    val fechaSolicitud: String,
    val estatus: Int,
    val idProducto: Int,
    val nombreProducto: String,
    val idUsuario: Int,
    val nombreCliente: String,
    val detalleSolicituds: List<DetalleSolicitud>,
    var nombreAsignado: String? = null

)