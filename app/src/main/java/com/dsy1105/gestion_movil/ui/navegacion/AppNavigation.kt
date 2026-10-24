package com.dsy1105.gestion_movil.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dsy1105.gestion_movil.ui.oportunidad.ListaOportunidadesScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadFormScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadViewModel

// =========================================================================================
// ARCHIVO DE NAVEGACIÓN PRINCIPAL: AppNavigation
//
// 📌 GUÍA Y NOTAS PARA EL COLABORADOR (MÓDULO DE LOGIN/AUTENTICACIÓN):
// Hola compañero! Actualmente la aplicación inicia directamente en la pantalla "lista"
// para probar el flujo de Gestión de Oportunidades.
//
// Cuando tengas lista tu pantalla de Login:
// 1. Cambia 'startDestination = "lista"' por 'startDestination = "login"'.
// 2. Agrega la ruta de tu pantalla de login aquí dentro:
//
//    composable("login") {
//        LoginScreen(
//            onLoginExitoso = {
//                navController.navigate("lista") {
//                    popUpTo("login") { inclusive = true } // Elimina login del historial
//                }
//            }
//        )
//    }
// =========================================================================================

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModel compartido para el módulo de oportunidades
    val oportunidadViewModel: OportunidadViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "lista" // Pantalla de inicio actual
    ) {

        // ---------------------------------------------------------------------------------
        // RUTA 1: Lista de Oportunidades (ListaOportunidadesScreen)
        // ---------------------------------------------------------------------------------
        composable("lista") {
            ListaOportunidadesScreen(
                viewModel = oportunidadViewModel,
                onNavegarAFormulario = { oportunidadId ->
                    if (oportunidadId != null && oportunidadId > 0L) {
                        // Modo Edición: Carga la oportunidad por su ID y navega al formulario
                        oportunidadViewModel.cargarOportunidadPorId(oportunidadId)
                        navController.navigate("formulario?oportunidadId=$oportunidadId")
                    } else {
                        // Modo Creación: Reinicia el estado del formulario y navega
                        oportunidadViewModel.resetEstadoFormulario()
                        navController.navigate("formulario")
                    }
                }
            )
        }

        // ---------------------------------------------------------------------------------
        // RUTA 2: Formulario de Oportunidad (OportunidadFormScreen) - Crear o Editar
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
            
            // Si venimos con un ID válido y no se ha cargado aún, lo cargamos
            if (idArg > 0L) {
                oportunidadViewModel.cargarOportunidadPorId(idArg)
            }

            OportunidadFormScreen(
                viewModel = oportunidadViewModel,
                onGuardadoExitoso = {
                    // Al guardar exitosamente, regresa a la pantalla de la lista
                    navController.popBackStack()
                },
                onNavigateBack = {
                    // Acción para volver atrás manualmente desde la barra superior
                    navController.popBackStack()
                }
            )
        }
    }
}
