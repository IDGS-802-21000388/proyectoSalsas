package com.example.proyecto.models

data class RegistroModel(
    val nombre: String,
    val nombreUsuario: String,
    val correo: String,
    val contrasenia: String,
    val telefono: String,
    val direccion: Direccion
)

data class Direccion(
    val estado: String,
    val municipio: String,
    val codigoPostal: String,
    val colonia: String,
    val calle: String,
    val numExt: String,
    val numInt: String?,
    val referencia: String?
)
