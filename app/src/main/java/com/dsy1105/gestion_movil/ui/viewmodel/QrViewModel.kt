package com.dsy1105.gestion_movil.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dsy1105.gestion_movil.data.model.QrResult
import com.dsy1105.gestion_movil.data.repository.QrRepository

// =========================================================================================
// VIEWMODEL: QrViewModel
// Estructura del profesor para gestionar el estado del resultado del código QR escaneado.
//
// Variables de la clase:
// - repository: Instancia de QrRepository.
// - _qrResult: Estado mutable del resultado QR (LiveData).
// - qrResult: Estado inmutable de lectura expuesto a la pantalla.
// =========================================================================================
class QrViewModel : ViewModel() {

    private val repository = QrRepository()

    private val _qrResult = MutableLiveData<QrResult?>()
    val qrResult: LiveData<QrResult?> = _qrResult

    // Función que se invoca cuando la cámara detecta un texto QR
    fun onQrDetected(content: String) {
        _qrResult.value = repository.processQrContent(content)
    }

    // Limpia el resultado del QR para permitir un nuevo escaneo
    fun clearResult() {
        _qrResult.value = null
    }
}
