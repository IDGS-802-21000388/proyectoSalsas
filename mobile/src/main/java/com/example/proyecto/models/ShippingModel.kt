package com.example.proyecto.models

data class ShippingModel(
    val idEnvio: Int,
    val estatusPedido: String,
    val fechaEnvio: String,
    val fechaEntregaEstimada: String,
    val estatusEnvio: String,
    val nombrePaqueteria: String,
    val nombreCliente: String,
    val nombreProducto: String,
    val domicilio: String,
    val total: Double
)
