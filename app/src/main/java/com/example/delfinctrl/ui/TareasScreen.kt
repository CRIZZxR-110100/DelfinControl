package com.example.delfinctrl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.Tarea
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.TareaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    tareaViewModel: TareaViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    modifier: Modifier = Modifier
) {
    val tareas by tareaViewModel.tareasState.collectAsState()
    val asignaturas by asignaturaViewModel.asignaturasState.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }
    var fechaStr by remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        mutableStateOf(sdf.format(Date()))
    }
    var selectedAsignatura by remember { mutableStateOf<Asignatura?>(null) }
    var expandedAsignatura by remember { mutableStateOf(false) }

    // Estado de filtros
    var filtroEstado by remember { mutableStateOf("Todas") } // "Todas", "Pendientes", "Completadas"
    var filtroAsignatura by remember { mutableStateOf<Asignatura?>(null) }
    var expandedFiltroAsignatura by remember { mutableStateOf(false) }

    // Al cargar o cambiar materias, seleccionar la primera por defecto para el formulario
    LaunchedEffect(asignaturas) {
        if (selectedAsignatura == null && asignaturas.isNotEmpty()) {
            selectedAsignatura = asignaturas.first()
        }
    }

    val tareasFiltradas = tareas.filter { tarea ->
        val cumpleEstado = when (filtroEstado) {
            "Pendientes" -> !tarea.estado
            "Completadas" -> tarea.estado
            else -> true
        }
        val cumpleAsignatura = if (filtroAsignatura != null) {
            tarea.asignaturaId == filtroAsignatura!!.asignaturaId
        } else {
            true
        }
        cumpleEstado && cumpleAsignatura
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tareas (To-Do)", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (asignaturas.isEmpty()) {
                // Advertencia si no hay materias registradas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "¡Atención!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Primero debes registrar al menos una materia en la pestaña 'Materias' para poder agregar tareas asociadas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                // Formulario de agregar tarea
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Agregar Nueva Tarea",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título de la Tarea") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = nota,
                            onValueChange = { nota = it },
                            label = { Text("Notas / Descripción (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = fechaStr,
                                onValueChange = { fechaStr = it },
                                label = { Text("Fecha (dd/mm/yyyy)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Dropdown para materia vinculada
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = selectedAsignatura?.nombre ?: "Seleccionar",
                                    onValueChange = {},
                                    label = { Text("Materia") },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { expandedAsignatura = true }
                                )

                                DropdownMenu(
                                    expanded = expandedAsignatura,
                                    onDismissRequest = { expandedAsignatura = false }
                                ) {
                                    asignaturas.forEach { asignatura ->
                                        DropdownMenuItem(
                                            text = { Text(asignatura.nombre) },
                                            onClick = {
                                                selectedAsignatura = asignatura
                                                expandedAsignatura = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val parsedDate = try {
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    sdf.parse(fechaStr)?.time ?: System.currentTimeMillis()
                                } catch (e: Exception) {
                                    System.currentTimeMillis()
                                }

                                if (titulo.isNotBlank() && selectedAsignatura != null) {
                                    tareaViewModel.guardarTarea(
                                        titulo = titulo.trim(),
                                        fecha = parsedDate,
                                        nota = nota.trim(),
                                        asignaturaId = selectedAsignatura!!.asignaturaId
                                    )
                                    titulo = ""
                                    nota = ""
                                }
                            },
                            enabled = titulo.isNotBlank() && selectedAsignatura != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Guardar Tarea")
                        }
                    }
                }
            }

            // Sección de Filtros
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Filtrar Tareas",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selector de Estado (Filtro)
                    val opcionesEstado = listOf("Todas", "Pendientes", "Completadas")
                    opcionesEstado.forEach { opcion ->
                        FilterChip(
                            selected = filtroEstado == opcion,
                            onClick = { filtroEstado = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }

                // Selector de Asignatura (Filtro)
                if (asignaturas.isNotEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            onClick = { expandedFiltroAsignatura = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (filtroAsignatura == null) "Todas las materias" else "Materia: ${filtroAsignatura!!.nombre}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = expandedFiltroAsignatura,
                            onDismissRequest = { expandedFiltroAsignatura = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todas las materias") },
                                onClick = {
                                    filtroAsignatura = null
                                    expandedFiltroAsignatura = false
                                }
                            )
                            asignaturas.forEach { asignatura ->
                                DropdownMenuItem(
                                    text = { Text(asignatura.nombre) },
                                    onClick = {
                                        filtroAsignatura = asignatura
                                        expandedFiltroAsignatura = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Lista de Tareas
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tareasFiltradas) { tarea ->
                    val nombreAsignatura = asignaturas.find { it.asignaturaId == tarea.asignaturaId }?.nombre ?: "Materia"
                    val fechaFormateada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(tarea.fechaDatetime))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (tarea.estado) {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = tarea.estado,
                                    onCheckedChange = { check ->
                                        tareaViewModel.cambiarEstadoTarea(tarea.tareaId, check)
                                    }
                                )

                                Column {
                                    Text(
                                        text = tarea.titulo,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            textDecoration = if (tarea.estado) TextDecoration.LineThrough else TextDecoration.None
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        color = if (tarea.estado) {
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (tarea.nota.isNotBlank()) {
                                        Text(
                                            text = tarea.nota,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = nombreAsignatura,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Vence: $fechaFormateada",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = { tareaViewModel.eliminarTarea(tarea) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar tarea",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
