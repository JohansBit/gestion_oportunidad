package com.dsy1105.gestion_movil.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dsy1105.gestion_movil.ui.oportunidad.RegistroScreen
import com.dsy1105.gestion_movil.ui.oportunidad.ResumenScreen
import com.dsy1105.gestion_movil.viewmodel.UsuarioViewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    val usuarioViewModel : UsuarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "registro"
    ){
        composable("registro"){
            RegistroScreen(navController = navController, viewModel = usuarioViewModel)
        }
        composable("resumen"){
            ResumenScreen(viewModel = usuarioViewModel)
        }
    }
}