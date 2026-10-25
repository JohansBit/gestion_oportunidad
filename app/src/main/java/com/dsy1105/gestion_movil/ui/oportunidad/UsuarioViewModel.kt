package com.dsy1105.gestion_movil.ui.oportunidad

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dsy1105.gestion_movil.datos.local.AppDataBase
import com.dsy1105.gestion_movil.datos.local.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuarioUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val direccion: String = "",
    val aceptaTerminos: Boolean = false,
    val nombreError: String? = null,
    val correoError: String? = null,
    val claveError: String? = null,
    val direccionError: String? = null,
    val loginError: String? = null,
    val cargando: Boolean = false
)

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDataBase.getDatabase(application).usuarioDao()

    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, nombreError = null) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, correoError = null, loginError = null) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, claveError = null, loginError = null) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(direccion = valor, direccionError = null) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarRegistro(): Boolean {
        val actual = _estado.value
        val nombreErr = if (actual.nombre.isBlank()) "Campo obligatorio" else null
        val correoErr = if (!actual.correo.contains("@")) "Correo inválido" else null
        val claveErr = if (actual.clave.length < 6) "Mínimo 6 caracteres" else null
        val direccionErr = if (actual.direccion.isBlank()) "Campo obligatorio" else null

        _estado.update {
            it.copy(
                nombreError = nombreErr,
                correoError = correoErr,
                claveError = claveErr,
                direccionError = direccionErr
            )
        }
        return listOfNotNull(nombreErr, correoErr, claveErr, direccionErr).isEmpty() && actual.aceptaTerminos
    }

    fun registrarUsuario(onExito: () -> Unit) {
        if (!validarRegistro()) return
        val actual = _estado.value
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val existe = dao.buscarPorCorreo(actual.correo.trim())
            if (existe != null) {
                _estado.update { it.copy(cargando = false, correoError = "El correo ya está registrado") }
            } else {
                dao.insertar(
                    UsuarioEntity(
                        nombre = actual.nombre.trim(),
                        correo = actual.correo.trim(),
                        clave = actual.clave.trim(),
                        direccion = actual.direccion.trim()
                    )
                )
                _estado.update { it.copy(cargando = false) }
                onExito()
            }
        }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val actual = _estado.value
        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _estado.update { it.copy(loginError = "Ingresa correo y contraseña") }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val usuario = dao.autenticar(actual.correo.trim(), actual.clave.trim())
            if (usuario != null) {
                _estado.update { it.copy(cargando = false, loginError = null) }
                onExito()
            } else {
                _estado.update { it.copy(cargando = false, loginError = "Credenciales incorrectas") }
            }
        }
    }
}

