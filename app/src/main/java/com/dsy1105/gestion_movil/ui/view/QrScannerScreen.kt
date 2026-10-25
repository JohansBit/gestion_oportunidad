package com.dsy1105.gestion_movil.ui.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dsy1105.gestion_movil.ui.viewmodel.QrViewModel
import com.dsy1105.gestion_movil.utils.QrScanner

// =========================================================================================
// PANTALLA: QrScannerScreen
// Estructura del profesor para la interfaz de lectura y escaneo de códigos QR.
//
// Parámetros:
// - viewModel: Instancia de QrViewModel.
// - hasCameraPermission: Valor booleano que indica si el permiso de cámara fue concedido.
// - onRequestPermission: Callback para solicitar el permiso al sistema.
// - onNavigateBack: Callback para regresar a la pantalla anterior.
// =========================================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    viewModel: QrViewModel,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    onQrAplicado: ((String) -> Unit)? = null
) {
    val qrResult by viewModel.qrResult.observeAsState()
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escaner de Código QR") },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Si no tiene permisos de cámara
            if (!hasCameraPermission) {
                Text(
                    text = "Permiso de cámara requerido",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Para escanear códigos QR de oportunidades, se necesita acceso a la cámara",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRequestPermission) {
                    Text("Conceder permiso de cámara")
                }
            }
            // 2. Si tiene permiso y está escaneando
            else if (qrResult == null && isScanning) {
                Text(
                    text = "Escanea un código QR",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Contenedor del visor de cámara CameraX
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    QrScanner(
                        onQrCodeScanned = { qrContent ->
                            // Procesar el QR detectado a través del ViewModel
                            viewModel.onQrDetected(qrContent)
                            isScanning = false
                            Toast.makeText(context, "¡Código QR detectado!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Marco visual overlay de escaneo en el centro
                    Surface(
                        modifier = Modifier
                            .size(250.dp)
                            .align(Alignment.Center),
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        )
                    ) {}
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enfoca el código QR en el marco central",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La cámara analizará el código automáticamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 3. Resultado del QR detectado
            else if (qrResult != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📱 QR Detectado:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = qrResult!!.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (onQrAplicado != null) {
                        Button(
                            onClick = {
                                onQrAplicado(qrResult!!.content)
                                viewModel.clearResult()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Usar este QR en la Oportunidad")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.clearResult()
                            isScanning = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escanear otro código QR")
                    }
                }
            }
        }
    }
}
