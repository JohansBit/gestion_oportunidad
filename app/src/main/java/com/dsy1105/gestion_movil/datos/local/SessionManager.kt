package com.dsy1105.gestion_movil.datos.local

import com.dsy1105.gestion_movil.datos.remote.UsuarioDto

object SessionManager {
    var usuarioActual: UsuarioDto? = null

    fun esSupervisor(): Boolean{
        return usuarioActual?.rol.equals("SUPERVISOR", ignoreCase = true)
    }

    fun getUserId(): Long{
        return usuarioActual?.id?: 0L
    }

    fun getUserName():String{
        return usuarioActual?.nombre?:""
    }
    fun cerrarSesion(){
        usuarioActual= null
    }
}