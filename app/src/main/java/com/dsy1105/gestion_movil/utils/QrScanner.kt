package com.dsy1105.gestion_movil.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean


// COMPOSABLE: QrScanner
// Estructura del profesor utilizando CameraX y ML Kit Barcode Scanning.
// Muestra la vista previa de la cámara y analiza cada fotograma en búsqueda de un código QR.
// Cuando detecta un QR válido, invoca el callback 'onQrCodeScanned'.


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun QrScanner(
    onQrCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Mantener actualizado el callback sin reiniciar CameraX
    val currentCallback = rememberUpdatedState(onQrCodeScanned)

    // Crear la vista previa de la cámara
    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // Integración de la vista tradicional de Android dentro de Jetpack Compose
    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize()
    )

    // Efecto para vincular y liberar la cámara al entrar y salir del Composable
    DisposableEffect(context, lifecycleOwner, previewView) {

        val cameraExecutor = Executors.newSingleThreadExecutor()

        // Configurar ML Kit para reconocer únicamente códigos QR
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)

        val disposed = AtomicBoolean(false)
        val detected = AtomicBoolean(false)

        // Configurar la vista previa
        val preview = Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

        // Configurar el análisis de imágenes de CameraX
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->

            if (disposed.get() || detected.get()) {
                imageProxy.close()
            } else {

                val mediaImage = imageProxy.image

                if (mediaImage == null) {
                    imageProxy.close()
                } else {

                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    try {
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->

                                val qr = barcodes.firstOrNull {
                                    it.format == Barcode.FORMAT_QR_CODE &&
                                            it.rawValue != null
                                }

                                if (qr != null && !disposed.get() && detected.compareAndSet(false, true)) {
                                    imageAnalysis.clearAnalyzer()

                                    currentCallback.value(qr.rawValue!!)

                                    Log.d("QrScanner", "Código QR detectado exitosamente")
                                }
                            }
                            .addOnFailureListener { error ->
                                Log.e("QrScanner", "Error al analizar código QR", error)
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }

                    } catch (error: Exception) {
                        Log.e("QrScanner", "Error al procesar la imagen", error)
                        imageProxy.close()
                    }
                }
            }
        }

        // Obtener el proveedor de CameraX y vincular con el ciclo de vida
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        var boundProvider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener({
            if (!disposed.get()) {
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    if (!disposed.get()) {
                        // Seleccionar la cámara trasera por defecto
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        // Vincular Preview e ImageAnalysis al ciclo de vida de la pantalla
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )

                        boundProvider = cameraProvider
                        Log.d("QrScanner", "CameraX vinculado correctamente")
                    }

                } catch (error: Exception) {
                    Log.e("QrScanner", "Error al iniciar la cámara", error)
                }
            }
        }, ContextCompat.getMainExecutor(context))

        // Liberar recursos al salir de la pantalla
        onDispose {
            disposed.set(true)
            imageAnalysis.clearAnalyzer()
            boundProvider?.unbind(preview, imageAnalysis)
            barcodeScanner.close()
            cameraExecutor.shutdown()
            Log.d("QrScanner", "Recursos de la cámara liberados")
        }
    }
}
