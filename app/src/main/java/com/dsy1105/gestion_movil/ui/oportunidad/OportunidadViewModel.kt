package com.dsy1105.gestion_movil.ui.oportunidad

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dsy1105.gestion_movil.datos.local.AppDataBase
import com.dsy1105.gestion_movil.datos.local.OportunidadEntity
import com.dsy1105.gestion_movil.datos.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * OportunidadViewModel
 *
 * Clase ViewModel encargada de gestionar la lógica de negocio y el estado de la UI 
 * para el módulo de Oportunidades, siguiendo el patrón de arquitectura MVVM.
 *
 * Hereda de AndroidViewModel para acceder al contexto de la aplicación e inicializar
 * la base de datos local de Room.
 */
class OportunidadViewModel(application: Application) : AndroidViewModel(application) {

    // Instancia del repositorio que centraliza las operaciones de datos (Room)
    private val repository: OportunidadRepository

    init {
        // Inicialización de la base de datos de Room y del repositorio
        val dao = AppDataBase.getDatabase(application).oportunidadDao()
        repository = OportunidadRepository(dao)
    }


    // ESTADO DE LA UI (StateFlow - Single Source of Truth)

    // Estado interno mutable (solo modificable dentro del ViewModel)
    private val _uiState = MutableStateFlow(OportunidadFormState(
        fechaDeteccion = obtenerFechaActual()
    ))

    // Estado público inmutable expuesto hacia las pantallas de Jetpack Compose
    val uiState: StateFlow<OportunidadFormState> = _uiState.asStateFlow()

    /**
     * Función auxiliar privada para obtener la fecha
     */
    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // EVENTOS DE ACTUALIZACIÓN DE CAMPOS

    fun onNombreChanged(nombre: String) {
        _uiState.update { 
            it.copy(
                nombre = nombre,
                nombreError = if (nombre.isBlank()) "El nombre es obligatorio" else null
            ) 
        }
    }

    fun onTipoChanged(tipo: String) {
        _uiState.update { 
            it.copy(
                tipo = tipo,
                tipoError = if (tipo.isBlank()) "Selecciona un tipo" else null
            ) 
        }
    }

    fun onDescripcionChanged(descripcion: String) {
        _uiState.update { 
            it.copy(
                descripcion = descripcion,
                descripcionError = if (descripcion.isBlank()) "La descripción es obligatoria" else null
            ) 
        }
    }

    fun onFuenteChanged(fuente: String) {
        _uiState.update { it.copy(fuente = fuente) }
    }

    fun onFechaDeteccionChanged(fechaDeteccion: String) {
        _uiState.update { it.copy(fechaDeteccion = fechaDeteccion) }
    }

    fun onResponsableInternoChanged(responsableInterno: String) {
        _uiState.update { 
            it.copy(
                responsableInterno = responsableInterno,
                responsableInternoError = if (responsableInterno.isBlank()) "El responsable interno es obligatorio" else null
            ) 
        }
    }

    fun onPrioridadChanged(prioridad: String) {
        _uiState.update { it.copy(prioridad = prioridad) }
    }

    fun onPotencialChanged(potencial: String) {
        _uiState.update { it.copy(potencial = potencial) }
    }

    fun onEstadoChanged(estado: String) {
        _uiState.update { it.copy(estado = estado) }
    }

    fun onObservacionesChanged(observaciones: String) {
        _uiState.update { it.copy(observaciones = observaciones) }
    }

    // 3. OPERACIONES DE LECTURA

    /**
     * Carga de forma asíncrona una oportunidad desde Room utilizando su ID
     * para poblar el formulario en modo de edición.
     */
    fun cargarOportunidadPorId(id: Long) {
        if (id <= 0L) return
        viewModelScope.launch {
            _uiState.update { it.copy(isGuardando = true) }
            val oportunidad = repository.obtenerPorId(id)
            if (oportunidad != null) {
                _uiState.update {
                    it.copy(
                        id = oportunidad.id,
                        nombre = oportunidad.nombre,
                        tipo = oportunidad.tipo,
                        descripcion = oportunidad.descripcion,
                        fuente = oportunidad.fuente,
                        fechaDeteccion = oportunidad.fechaDeteccion.ifBlank { obtenerFechaActual() },
                        responsableInterno = oportunidad.responsableInterno,
                        prioridad = oportunidad.prioridad,
                        potencial = oportunidad.potencial,
                        estado = oportunidad.estado,
                        fechaActualizacion = oportunidad.fechaActualizacion,
                        observaciones = oportunidad.observaciones,
                        isGuardando = false
                    )
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isGuardando = false,
                        mensajeError = "No se encontró la oportunidad con id $id"
                    ) 
                }
            }
        }
    }
    // 4. LÓGICA DE NEGOCIO Y PERSISTENCIA Validación y Guardado


     // Valida la presencia de campos obligatorios  y guarda o actualiza
    fun guardarOportunidad() {
        val currentState = _uiState.value
        
        // Validación de campos obligatorios según reglas de negocio
        val esNombreValido = currentState.nombre.isNotBlank()
        val esTipoValido = currentState.tipo.isNotBlank()
        val esDescripcionValida = currentState.descripcion.isNotBlank()
        val esResponsableValido = currentState.responsableInterno.isNotBlank()

        // Si falta algún campo obligatorio, se detiene el proceso y se emiten mensajes de error
        if (!esNombreValido || !esTipoValido || !esDescripcionValida || !esResponsableValido) {
            _uiState.update { 
                it.copy(
                    nombreError = if (!esNombreValido) "El nombre es obligatorio" else null,
                    tipoError = if (!esTipoValido) "Selecciona o ingresa un tipo" else null,
                    descripcionError = if (!esDescripcionValida) "La descripción es obligatoria" else null,
                    responsableInternoError = if (!esResponsableValido) "El responsable interno es obligatorio" else null,
                    mensajeError = "Por favor complete todos los campos obligatorios (*)"
                ) 
            }
            return
        }

        // Ejecución en segundo plano mediante corrutinas
        viewModelScope.launch {
            _uiState.update { it.copy(isGuardando = true, mensajeError = null) }
            try {
                val fechaActual = obtenerFechaActual()
                // Creación de la entidad Room con los valores actuales del estado
                val entidad = OportunidadEntity(
                    id = currentState.id,
                    nombre = currentState.nombre.trim(),
                    tipo = currentState.tipo.trim(),
                    descripcion = currentState.descripcion.trim(),
                    fuente = currentState.fuente.trim(),
                    fechaDeteccion = currentState.fechaDeteccion.ifBlank { fechaActual },
                    responsableInterno = currentState.responsableInterno.trim(),
                    prioridad = currentState.prioridad,
                    potencial = currentState.potencial,
                    estado = currentState.estado,
                    fechaActualizacion = fechaActual,
                    observaciones = currentState.observaciones.trim()
                )
                
                // Guardado/Actualización en la base de datos a través del repositorio
                repository.guardar(entidad)

                // Notificación de éxito a la UI
                _uiState.update {
                    it.copy(
                        isGuardando = false,
                        isGuardadoExitoso = true,
                        mensajeExito = if (currentState.id == 0L) "¡Oportunidad registrada exitosamente!" else "¡Oportunidad actualizada exitosamente!"
                    )
                }
            } catch (e: Exception) {
                // Manejo de excepciones en la persistencia
                _uiState.update {
                    it.copy(
                        isGuardando = false,
                        mensajeError = "Error al guardar en la base de datos: ${e.localizedMessage ?: "Error desconocido"}"
                    )
                }
            }
        }
    }
    // RESET Y LIMPIEZA DE ESTADO

    /**
     * Reinicia el estado del formulario a sus valores por defecto.
     */
    fun resetEstadoFormulario() {
        _uiState.value = OportunidadFormState(
            fechaDeteccion = obtenerFechaActual()
        )
    }

    /**
     * Limpia el mensaje de error para evitar repetición en la UI.
     */
    fun consumirMensajeError() {
        _uiState.update { it.copy(mensajeError = null) }
    }
}
