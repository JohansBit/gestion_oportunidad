package com.dsy1105.gestion_movil.datos.remote

data class UsuarioDto (
    val id:Long = 0L,
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val rol: String= "EJECUTIVO"
)