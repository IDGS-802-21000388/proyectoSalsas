package com.example.proyecto.models

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val nombreUsuario: String,
    val contrasenia: String,
    val rol: String,
    val estatus: Int,
    val telefono: String,
    val intentos: Int,
    val dateLastToken: String
)
