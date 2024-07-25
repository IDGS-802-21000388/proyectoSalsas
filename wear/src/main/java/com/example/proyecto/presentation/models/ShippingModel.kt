package com.example.proyecto.presentation.models

data class ShippingModel(
    val idEnvio: Int,
    val estatusPedido: Int,
    val fechaEnvio: String,
    val fechaEntregaEstimada: String,
    val estatusEnvio: String,
    val nombrePaqueteria: String,
    val nombreCliente: String,
    val nombreProducto: String,
    val domicilio: String,
    val total: Double
)