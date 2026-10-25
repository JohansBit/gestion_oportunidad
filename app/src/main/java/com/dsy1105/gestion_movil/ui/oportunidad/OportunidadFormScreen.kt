package com.dsy1105.gestion_movil.ui.oportunidad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    viewModel: OportunidadViewModel,
    oportunidadId: Long = 0L,
    onNavigateBack: (() -> Unit)? = null,
    onGuardadoExitoso: (() -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(oportunidadId) {
        if (oportunidadId > 0L) {
            viewModel.cargarOportunidadPorId(oportunidadId)
        }
    }

    LaunchedEffect(state.mensajeError) {
        state.mensajeError?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumirMensajeError()
        }
    }

    if (state.isGuardadoExitoso) {
        AlertDialog(
            onDismissRequest = { },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = { Text(text = "¡Éxito!") },
            text = { Text(text = state.mensajeExito ?: "La oportunidad fue guardada correctamente.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (onGuardadoExitoso != null) {
                            viewModel.resetEstadoFormulario()
                            onGuardadoExitoso()
                        } else {
                            viewModel.resetEstadoFormulario()
                        }
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                if (onGuardadoExitoso != null) {
                    OutlinedButton(
                        onClick = {
                            viewModel.resetEstadoFormulario()
                        }
                    ) {
                        Text("Registrar otra")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.id > 0) "Editar Oportunidad" else "Nueva Oportunidad"
                    )
                },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // : Información de la Oportunidad
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Información de la Oportunidad",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Nombre de la Oportunidad
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = viewModel::onNombreChanged,
                        label = { Text("Nombre de la Oportunidad *") },
                        isError = state.nombreError != null,
                        supportingText = {
                            state.nombreError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Tipo de Oportunidad (Exposed Dropdown)
                    var tipoExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = tipoExpanded,
                        onExpandedChange = { tipoExpanded = !tipoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.tipo,
                            onValueChange = viewModel::onTipoChanged,
                            label = { Text("Tipo de Oportunidad *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                            isError = state.tipoError != null,
                            supportingText = {
                                state.tipoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                            },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = tipoExpanded,
                            onDismissRequest = { tipoExpanded = false }
                        ) {
                            OportunidadOpciones.tipos.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        viewModel.onTipoChanged(opcion)
                                        tipoExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Descripción
                    OutlinedTextField(
                        value = state.descripcion,
                        onValueChange = viewModel::onDescripcionChanged,
                        label = { Text("Descripción *") },
                        isError = state.descripcionError != null,
                        supportingText = {
                            state.descripcionError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }

            // 2: Gestión y Asignación
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Gestión y Responsables",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Responsable Interno
                    OutlinedTextField(
                        value = state.responsableInterno,
                        onValueChange = viewModel::onResponsableInternoChanged,
                        label = { Text("Responsable Interno *") },
                        placeholder = { Text("Ej: Ejecutivo asignado") },
                        isError = state.responsableInternoError != null,
                        supportingText = {
                            state.responsableInternoError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Fuente de información
                    OutlinedTextField(
                        value = state.fuente,
                        onValueChange = viewModel::onFuenteChanged,
                        label = { Text("Fuente de Información") },
                        placeholder = { Text("Ej: Redes sociales, Sitio web, Contacto directo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Fecha de Detección
                    OutlinedTextField(
                        value = state.fechaDeteccion,
                        onValueChange = viewModel::onFechaDeteccionChanged,
                        label = { Text("Fecha de Detección (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // 3: Clasificación y Estado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Clasificación y Estado de la Gestión",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Nivel de Prioridad (Chips)
                    Text(text = "Nivel de Prioridad", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OportunidadOpciones.prioridades.forEach { item ->
                            val selected = state.prioridad.equals(item, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onPrioridadChanged(item) },
                                label = { Text(item) },
                                leadingIcon = if (selected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }

                    // Potencial Comercial Estimado (Chips)
                    Text(text = "Potencial Comercial Estimado", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OportunidadOpciones.potenciales.forEach { item ->
                            val selected = state.potencial.equals(item, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.onPotencialChanged(item) },
                                label = { Text(item) },
                                leadingIcon = if (selected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }

                    // Estado de la Gestión (Exposed Dropdown)
                    var estadoExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = estadoExpanded,
                        onExpandedChange = { estadoExpanded = !estadoExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.estado,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Estado de la Gestión") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = estadoExpanded,
                            onDismissRequest = { estadoExpanded = false }
                        ) {
                            OportunidadOpciones.estados.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        viewModel.onEstadoChanged(opcion)
                                        estadoExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 4: Observaciones
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Observaciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = state.observaciones,
                        onValueChange = viewModel::onObservacionesChanged,
                        label = { Text("Observaciones del proceso") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = viewModel::resetEstadoFormulario,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isGuardando
                ) {
                    Text("Limpiar")
                }

                Button(
                    onClick = viewModel::guardarOportunidad,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isGuardando
                ) {
                    if (state.isGuardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Guardar")
                }
            }
        }
    }
}
