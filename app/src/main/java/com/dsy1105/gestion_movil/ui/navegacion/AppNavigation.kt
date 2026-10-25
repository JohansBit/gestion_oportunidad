package com.dsy1105.gestion_movil.ui.navegacion

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dsy1105.gestion_movil.ui.oportunidad.ListaOportunidadesScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadFormScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadViewModel
import com.dsy1105.gestion_movil.ui.view.QrScannerScreen
import com.dsy1105.gestion_movil.ui.viewmodel.QrViewModel
import com.dsy1105.gestion_movil.utils.CameraPermissionHelper

// =========================================================================================
// ARCHIVO DE NAVEGACIÓN PRINCIPAL: AppNavigation
//
// 📌 GUÍA Y NOTAS PARA EL COLABORADOR (MÓDULO DE LOGIN/AUTENTICACIÓN):
// Hola compañero! Actualmente la aplicación inicia directamente en la pantalla "lista"
// para probar el flujo de Gestión de Oportunidades y el Escáner QR.
//
// Cuando tengas lista tu pantalla de Login:
// 1. Cambia 'startDestination = "lista"' por 'startDestination = "login"'.
// 2. Agrega la ruta de tu pantalla de login aquí dentro:
//
//    composable("login") {
//        LoginScreen(
//            onLoginExitoso = {
//                navController.navigate("lista") {
//                    popUpTo("login") { inclusive = true }
//                }
//            }
//        )
//    }
// =========================================================================================

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels compartidos
    val oportunidadViewModel: OportunidadViewModel = viewModel()
    val qrViewModel: QrViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "lista" // Pantalla inicial actual
    ) {

        // ---------------------------------------------------------------------------------
        // RUTA 1: Lista de Oportunidades
        // ---------------------------------------------------------------------------------
        composable("lista") {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,
                onNavegarAFormulario = { oportunidadId ->
                    if (oportunidadId != null && oportunidadId > 0L) {
                        oportunidadViewModel.cargarOportunidadPorId(oportunidadId)
                        navController.navigate("formulario?oportunidadId=$oportunidadId")
                    } else {
                        oportunidadViewModel.resetEstadoFormulario()
                        navController.navigate("formulario")
                    }
                },
                onNavegarAEscaner = {
                    navController.navigate("escaner_qr")
                }
            )
        }

        // ---------------------------------------------------------------------------------
        // RUTA 2: Formulario de Oportunidad (Crear / Editar)
        // ---------------------------------------------------------------------------------
        composable(
            route = "formulario?oportunidadId={oportunidadId}",
            arguments = listOf(
                navArgument("oportunidadId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getLong("oportunidadId") ?: -1L
            
            if (idArg > 0L) {
                oportunidadViewModel.cargarOportunidadPorId(idArg)
            }

            OportunidadFormScreen(
                viewModel = oportunidadViewModel,
                onGuardadoExitoso = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------------------------------------------------------------------------
        // RUTA 3: Escáner de Código QR (Recurso nativo - Cámara)
        // ---------------------------------------------------------------------------------
        composable("escaner_qr") {
            val context = LocalContext.current
            var hasCameraPermission by remember {
                mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                hasCameraPermission = isGranted
            }

            QrScannerScreen(
                viewModel = qrViewModel,
                hasCameraPermission = hasCameraPermission,
                onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onQrAplicado = { qrContent ->
                    // Al escanear un QR exitosamente, se puede asignar como fuente o observaciones
                    oportunidadViewModel.onFuenteChanged("QR: $qrContent")
                    navController.popBackStack()
                }
            )
        }
    }
}
