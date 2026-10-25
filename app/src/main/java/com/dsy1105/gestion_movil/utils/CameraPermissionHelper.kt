package com.dsy1105.gestion_movil.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

// HELPER DE PERMISOS: CameraPermissionHelper
// Estructura del profesor para verificar si la app tiene permiso de usar la Cámara.
object CameraPermissionHelper {

    // Comprueba si el permiso Manifest.permission.CAMERA fue concedido por el usuario
    fun hasCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}
