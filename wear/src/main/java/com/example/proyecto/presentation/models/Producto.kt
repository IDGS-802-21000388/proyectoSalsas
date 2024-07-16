package com.example.proyecto.models

data class Producto(
    val idProducto: Int,
    val nombreProducto: String,
    val precioVenta: Float,
    val precioProduccion: Float,
    val idMedida: Int,
    val fotografia: String,
    val estatus: Boolean
)
