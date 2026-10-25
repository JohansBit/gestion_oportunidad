package com.dsy1105.gestion_movil.data.repository

import com.dsy1105.gestion_movil.data.model.QrResult

// =========================================================================================
// REPOSITORIO: QrRepository
// Estructura del profesor encargada de procesar el contenido de los códigos QR.
// =========================================================================================
class QrRepository {

    // Función sencilla que recibe el texto del QR y retorna el objeto QrResult
    fun processQrContent(content: String): QrResult {
        return QrResult(content)
    }
}
