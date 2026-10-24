package com.dsy1105.gestion_movil.ui.oportunidad

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsy1105.gestion_movil.datos.local.OportunidadEntity

//  ListaOporpantalla tunidadesScreen
// Módulo de Gestión de Oportunidades
// Esta pantalla observa en tiempo real las oportunidades registradas en Room a través de
// 'viewModel.listaOportunidades'.
//
// Parámetros:
// - viewModel: Instancia del ViewModel de oportunidades.
// - onNavegarAFormulario: Callback para navegar al formulario. Pasa 'null' para crear
//   una nueva oportunidad, o pasa el 'id: Long' para editar una oportunidad existente.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaOportunidadesScreen(
    viewModel: OportunidadViewModel,
    onNavegarAFormulario: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Observación reactiva de la lista de oportunidades desde Room
    val listaOportunidades by viewModel.listaOportunidades.collectAsState()

    // Estados locales para filtro de búsqueda y prioridad
    var textoBusqueda by remember { mutableStateOf("") }
    var prioridadFiltro by remember { mutableStateOf("Todas") }

    // Filtrado en memoria de la lista según el texto de búsqueda y prioridad seleccionada
    val listaFiltrada = remember(listaOportunidades, textoBusqueda, prioridadFiltro) {
        listaOportunidades.filter { oportunidad ->
            val coincideTexto = oportunidad.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    oportunidad.tipo.contains(textoBusqueda, ignoreCase = true) ||
                    oportunidad.descripcion.contains(textoBusqueda, ignoreCase = true)

            val coincidePrioridad = prioridadFiltro == "Todas" ||
                    oportunidad.prioridad.equals(prioridadFiltro, ignoreCase = true)

            coincideTexto && coincidePrioridad
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Oportunidades Corporativas",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${listaOportunidades.size} registros almacenados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        floatingActionButton = {
            // Botón flotante para crear una nueva oportunidad
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.resetEstadoFormulario()
                    onNavegarAFormulario(null) // null indica modo creación
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar") },
                text = { Text("Nueva Oportunidad") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 2. Barra de búsqueda y filtros rápidos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por nombre, tipo o descripción...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (textoBusqueda.isNotEmpty()) {
                            IconButton(onClick = { textoBusqueda = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Filtros por prioridad
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val opcionesPrioridad = listOf("Todas", "Alta", "Media", "Baja")
                    items(opcionesPrioridad) { prioridad ->
                        FilterChip(
                            selected = prioridadFiltro == prioridad,
                            onClick = { prioridadFiltro = prioridad },
                            label = { Text(prioridad) }
                        )
                    }
                }
            }

            // Contenido principal: Lista de oportunidades o Estado Vacío
            if (listaFiltrada.isEmpty()) {
                EstadoVacioOportunidades(
                    hayTextoBusqueda = textoBusqueda.isNotEmpty() || prioridadFiltro != "Todas",
                    onLimpiarFiltros = {
                        textoBusqueda = ""
                        prioridadFiltro = "Todas"
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = listaFiltrada,
                        key = { it.id }
                    ) { oportunidad ->
                        OportunidadCardItem(
                            oportunidad = oportunidad,
                            onClick = {
                                onNavegarAFormulario(oportunidad.id) // Navega al modo edición
                            }
                        )
                    }
                }
            }
        }
    }
}

// COMPOSABLE: Tarjeta de Oportunidad individual (OportunidadCardItem)

@Composable
private fun OportunidadCardItem(
    oportunidad: OportunidadEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Encabezado Nombre y Chip de Prioridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = oportunidad.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ChipPrioridad(prioridad = oportunidad.prioridad)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Tipo de oportunidad
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = oportunidad.tipo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Descripción breve
            if (oportunidad.descripcion.isNotBlank()) {
                Text(
                    text = oportunidad.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Pie de tarjeta Estado de gestión, Fecha y Responsable
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipEstado(estado = oportunidad.estado)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (oportunidad.fechaDeteccion.isNotBlank()) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = oportunidad.fechaDeteccion,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (oportunidad.responsableInterno.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = oportunidad.responsableInterno,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// COMPOSABLES DE APOYO Chips de Prioridad y Estado
@Composable
private fun ChipPrioridad(prioridad: String) {
    val (colorFondo, colorTexto) = when (prioridad.lowercase()) {
        "alta" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        "media" -> Pair(Color(0xFFFFF8E1), Color(0xFFF57F17))
        "baja" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        else -> Pair(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Prioridad: $prioridad",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = colorTexto
        )
    }
}

@Composable
private fun ChipEstado(estado: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = estado,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}
// COMPOSABLE: Estado Vacío (Empty State)
@Composable
private fun EstadoVacioOportunidades(
    hayTextoBusqueda: Boolean,
    onLimpiarFiltros: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hayTextoBusqueda) "No se encontraron coincidencias" else "No hay oportunidades registradas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (hayTextoBusqueda)
                    "Prueba cambiando el texto de búsqueda o los filtros seleccionados."
                else
                    "Presiona el botón '+' para agregar la primera oportunidad comercial.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (hayTextoBusqueda) {
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(onClick = onLimpiarFiltros) {
                    Text(
                        text = "Limpiar filtros",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
