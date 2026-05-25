package com.example.delfinctrl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    asignaturaViewModel: AsignaturaViewModel,
    modifier: Modifier = Modifier
) {
    val tareas by asignaturaViewModel.tareasState.collectAsState()
    val asignaturas by asignaturaViewModel.asignaturasState.collectAsState()

    val context = LocalContext.current
    var tareaToEdit by remember { mutableStateOf<com.example.delfinctrl.data.model.Tarea?>(null) }
    var showAddTarea by remember { mutableStateOf(false) }

    // Lógica para filtrar tareas según el estado (pendientes/completadas) y la materia
    var filtroEstado by remember { mutableStateOf("Todas") } // "Todas", "Pendientes", "Completadas"
    var filtroAsignatura by remember { mutableStateOf<Asignatura?>(null) }
    var expandedFiltroAsignatura by remember { mutableStateOf(false) }

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
        },
        floatingActionButton = {
            if (asignaturas.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showAddTarea = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
                }
            }
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
                // Sección de Filtros
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filtrar Tareas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedButton(
                            onClick = {
                                val builder = androidx.core.app.NotificationCompat.Builder(context, "TAREAS_CHANNEL")
                                    .setSmallIcon(com.example.delfinctrl.R.mipmap.ic_launcher)
                                    .setContentTitle("Prueba de Notificación")
                                    .setContentText("¡Las notificaciones están funcionando correctamente!")
                                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                                    .setAutoCancel(true)

                                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    androidx.core.app.NotificationManagerCompat.from(context).notify(999, builder.build())
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Probar Notificaciones", style = MaterialTheme.typography.labelSmall)
                        }
                    }

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
                                        asignaturaViewModel.cambiarEstadoTarea(tarea.tareaId, check)
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

                            Row {
                                IconButton(onClick = { 
                                    tareaToEdit = tarea
                                    showAddTarea = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar tarea",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { asignaturaViewModel.eliminarTarea(tarea, context) }) {
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

    if (showAddTarea) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { 
                showAddTarea = false
                tareaToEdit = null
            },
            sheetState = sheetState
        ) {
            AgregarTareaForm(asignaturaViewModel, asignaturas, tareaToEdit) { 
                showAddTarea = false
                tareaToEdit = null
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaForm(
    asignaturaViewModel: AsignaturaViewModel,
    asignaturas: List<Asignatura>,
    tareaToEdit: com.example.delfinctrl.data.model.Tarea? = null,
    onDismiss: () -> Unit
) {
    var titulo by remember(tareaToEdit) { mutableStateOf(tareaToEdit?.titulo ?: "") }
    var nota by remember(tareaToEdit) { mutableStateOf(tareaToEdit?.nota ?: "") }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = tareaToEdit?.fechaDatetime ?: System.currentTimeMillis())
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val fechaElegida = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val fechaStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaElegida))

    var selectedAsignatura by remember(tareaToEdit) { 
        mutableStateOf(if (tareaToEdit != null) asignaturas.find { it.asignaturaId == tareaToEdit.asignaturaId } else asignaturas.firstOrNull()) 
    }
    var expandedAsignatura by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (tareaToEdit == null) "Agregar Nueva Tarea" else "Editar Tarea",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la Tarea") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            label = { Text("Notas / Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fechaStr,
                    onValueChange = {},
                    label = { Text("Fecha") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = selectedAsignatura?.nombre ?: "Seleccionar",
                    onValueChange = {},
                    label = { Text("Materia") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedAsignatura = true })

                DropdownMenu(expanded = expandedAsignatura, onDismissRequest = { expandedAsignatura = false }) {
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
                if (titulo.isNotBlank() && selectedAsignatura != null) {
                    if (tareaToEdit == null) {
                        asignaturaViewModel.guardarTarea(
                            titulo = titulo.trim(),
                            fecha = fechaElegida,
                            nota = nota.trim(),
                            asignaturaId = selectedAsignatura!!.asignaturaId,
                            context = context
                        )
                    } else {
                        asignaturaViewModel.actualizarTarea(
                            tareaToEdit.copy(
                                titulo = titulo.trim(),
                                fechaDatetime = fechaElegida,
                                nota = nota.trim(),
                                asignaturaId = selectedAsignatura!!.asignaturaId
                            ),
                            context = context
                        )
                    }
                    onDismiss()
                }
            },
            enabled = titulo.isNotBlank() && selectedAsignatura != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (tareaToEdit == null) "Guardar Tarea" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
