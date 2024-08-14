package com.example.proyecto.models

data class Usuario(
    val idUsuario: Int,
    val nombre: String,
    val nombreUsuario: String,
    val correo: String,
    val contrasenia: String,
    val rol: String,
    val estatus: Int,
    val telefono: String,
    val intentos: Int,
    val idDireccion: Int,
    val dateLastToken: String,
    val direccion: Any?
)

data class LoginResponse(
    val message: String,
    val user: Usuario
)