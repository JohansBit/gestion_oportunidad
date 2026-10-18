package com.dsy1105.gestion_movil.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadFormScreen
import com.dsy1105.gestion_movil.ui.oportunidad.OportunidadViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val oportunidadViewModel: OportunidadViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "formulario"
    ) {
        composable("formulario") {
            OportunidadFormScreen(
                viewModel = oportunidadViewModel,
                onGuardadoExitoso = {
                    // Opcional: Reiniciar o navegar
                }
            )
        }
    }
}
