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
import com.dsy1105.gestion_movil.ui.oportunidad.LoginUsuarioScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadFormScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadViewModel
import com.dsy1105.gestion_movil.ui.oportunidad.RegistroUsuarioScreen
import com.dsy1105.gestion_movil.ui.oportunidad.UsuarioViewModel
import com.dsy1105.gestion_movil.ui.view.QrScannerScreen
import com.dsy1105.gestion_movil.ui.viewmodel.QrViewModel
import com.dsy1105.gestion_movil.utils.CameraPermissionHelper

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels compartidos
    val oportunidadViewModel: OportunidadViewModel = viewModel()
    val qrViewModel: QrViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login" // Inicia en el Login
    ) {

        // RUTA 0A: Iniciar Sesión (Login)
        composable("login") {
            LoginUsuarioScreen(
                viewModel = usuarioViewModel,
                onLoginExitoso = {
                    navController.navigate("lista") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate("registro")
                }
            )
        }

        // RUTA 0B: Registro de Usuario
        composable("registro") {
            RegistroUsuarioScreen(
                viewModel = usuarioViewModel,
                onRegistroExitoso = {
                    // Al registrarse exitosamente, entra directo a la lista
                    navController.navigate("lista") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onIrALogin = {
                    navController.popBackStack()
                }
            )
        }

        // RUTA 1: Lista de Oportunidades
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

        // RUTA 2: Formulario de Oportunidad (Crear / Editar)
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

        // RUTA 3: Escáner de Código QR (Recurso nativo - Cámara)
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
                    oportunidadViewModel.onFuenteChanged("QR: $qrContent")
                    navController.popBackStack()
                }
            )
        }
    }
}