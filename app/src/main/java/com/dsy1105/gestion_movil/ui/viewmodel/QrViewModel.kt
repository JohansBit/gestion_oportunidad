package com.dsy1105.gestion_movil.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dsy1105.gestion_movil.data.model.QrResult
import com.dsy1105.gestion_movil.data.repository.QrRepository
import com.dsy1105.gestion_movil.datos.local.AppDataBase
import com.dsy1105.gestion_movil.datos.local.OportunidadEntity
import com.dsy1105.gestion_movil.datos.repository.OportunidadRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// VIEWMODEL: QrViewModel
// Gestiona el resultado del código QR escaneado y lo almacena en la base de datos local (Room).
class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QrRepository()
    private val oportunidadRepository: OportunidadRepository

    init {
        val dao = AppDataBase.getDatabase(application).oportunidadDao()
        oportunidadRepository = OportunidadRepository(dao)
    }

    private val _qrResult = MutableLiveData<QrResult?>()
    val qrResult: LiveData<QrResult?> = _qrResult

    // Función que se invoca cuando la cámara detecta un texto QR y lo guarda en la BD
    fun onQrDetected(content: String) {
        _qrResult.value = repository.processQrContent(content)
        viewModelScope.launch {
            try {
                val entidad = OportunidadEntity(
                    nombre = "Oportunidad QR",
                    tipo = "Escaneo QR",
                    descripcion = content,
                    fuente = "Código QR",
                    fechaDeteccion = obtenerFechaActual(),
                    responsableInterno = "Sistema QR",
                    estado = "Evento identificado"
                )
                oportunidadRepository.guardar(entidad)
            } catch (e: Exception) {
                Log.e("QrViewModel", "Error al guardar oportunidad QR", e)
            }
        }
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // Limpia el resultado del QR para permitir un nuevo escaneo
    fun clearResult() {
        _qrResult.value = null
    }
}
