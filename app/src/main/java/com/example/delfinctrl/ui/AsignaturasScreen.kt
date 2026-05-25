package com.example.delfinctrl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturasScreen(
    asignaturaViewModel: AsignaturaViewModel,
    modifier: Modifier = Modifier
) {
    var selectedAsignaturaDetails by remember { mutableStateOf<Asignatura?>(null) }

    if (selectedAsignaturaDetails != null) {
        AsignaturaDetailsFullScreen(
            asignatura = selectedAsignaturaDetails!!,
            asignaturaViewModel = asignaturaViewModel,
            onBack = { selectedAsignaturaDetails = null }
        )
        return
    }

    val asignaturas by asignaturaViewModel.asignaturasState.collectAsState()
    val context = LocalContext.current
    val totalCreditos by asignaturaViewModel.creditosTotalesState.collectAsState()
    val docentes by asignaturaViewModel.docentesState.collectAsState()

    var showAddMateria by remember { mutableStateOf(false) }
    var asignaturaToEdit by remember { mutableStateOf<Asignatura?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Plan de Estudios", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddMateria = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
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
            // Resumen de Créditos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Créditos Aprobados:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$totalCreditos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Materias Registradas (${asignaturas.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(asignaturas) { asignatura ->
                    val previaNombre = asignaturas.find { it.asignaturaId == asignatura.previa }?.nombre ?: "Ninguna"
                    val docenteAsignado = docentes.find { it.docenteId == asignatura.docenteId }
                    val docenteNombre = docenteAsignado?.let { "${it.nombre} ${it.apellidos}" } ?: "Sin asignar"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAsignaturaDetails = asignatura },
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = asignatura.nombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Profesor: $docenteNombre", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(text = "Créditos: ${asignatura.creditos}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Previa: $previaNombre", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row {
                                IconButton(onClick = { 
                                    asignaturaToEdit = asignatura
                                    showAddMateria = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { asignaturaViewModel.eliminarAsignatura(asignatura) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddMateria) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { 
            showAddMateria = false
            asignaturaToEdit = null
        }, sheetState = sheetState) {
            AgregarMateriaForm(asignaturaViewModel, asignaturas, docentes, asignaturaToEdit) { 
                showAddMateria = false
                asignaturaToEdit = null
            }
        }
    }
}

@Composable
fun AgregarMateriaForm(
    asignaturaViewModel: AsignaturaViewModel,
    asignaturas: List<Asignatura>,
    docentes: List<Docente>,
    asignaturaToEdit: Asignatura? = null,
    onDismiss: () -> Unit
) {
    var nombre by remember(asignaturaToEdit) { mutableStateOf(asignaturaToEdit?.nombre ?: "") }
    var creditosStr by remember(asignaturaToEdit) { mutableStateOf(asignaturaToEdit?.creditos?.toString() ?: "") }
    var selectedPrevia by remember(asignaturaToEdit) { mutableStateOf(if (asignaturaToEdit != null) asignaturas.find { it.asignaturaId == asignaturaToEdit.previa } else null) }
    var expandedPrevia by remember { mutableStateOf(false) }
    var selectedDocente by remember(asignaturaToEdit) { mutableStateOf(if (asignaturaToEdit != null) docentes.find { it.docenteId == asignaturaToEdit.docenteId } else null) }
    var expandedDocente by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(if (asignaturaToEdit == null) "Agregar Nueva Materia" else "Editar Materia", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la Materia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = creditosStr,
            onValueChange = { creditosStr = it },
            label = { Text("Créditos") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        // Selector de Prerrequisito
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedPrevia?.nombre ?: "Ninguna",
                onValueChange = {},
                label = { Text("Materia Previa") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
            )
            Box(modifier = Modifier.matchParentSize().clickable { expandedPrevia = true })

            DropdownMenu(expanded = expandedPrevia, onDismissRequest = { expandedPrevia = false }) {
                DropdownMenuItem(text = { Text("Ninguna") }, onClick = { selectedPrevia = null; expandedPrevia = false })
                asignaturas.forEach { asig ->
                    DropdownMenuItem(text = { Text(asig.nombre) }, onClick = { selectedPrevia = asig; expandedPrevia = false })
                }
            }
        }

        // Selector de Docente
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedDocente?.let { "${it.nombre} ${it.apellidos}" } ?: "Sin asignar",
                onValueChange = {},
                label = { Text("Docente / Profesor") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
            )
            Box(modifier = Modifier.matchParentSize().clickable { expandedDocente = true })

            DropdownMenu(expanded = expandedDocente, onDismissRequest = { expandedDocente = false }) {
                DropdownMenuItem(text = { Text("Sin asignar") }, onClick = { selectedDocente = null; expandedDocente = false })
                docentes.forEach { docente ->
                    DropdownMenuItem(text = { Text("${docente.nombre} ${docente.apellidos}") }, onClick = { selectedDocente = docente; expandedDocente = false })
                }
            }
        }

        Button(
            onClick = {
                val creditos = creditosStr.toIntOrNull() ?: 0
                if (nombre.isNotBlank()) {
                    if (asignaturaToEdit == null) {
                        asignaturaViewModel.guardarAsignatura(nombre.trim(), creditos, selectedPrevia?.asignaturaId, selectedDocente?.docenteId)
                    } else {
                        asignaturaViewModel.actualizarAsignatura(
                            asignaturaToEdit.copy(
                                nombre = nombre.trim(),
                                creditos = creditos,
                                previa = selectedPrevia?.asignaturaId,
                                docenteId = selectedDocente?.docenteId
                            )
                        )
                    }
                    onDismiss()
                }
            },
            enabled = nombre.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (asignaturaToEdit == null) "Registrar Materia" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturaDetailsFullScreen(
    asignatura: Asignatura,
    asignaturaViewModel: AsignaturaViewModel,
    onBack: () -> Unit
) {
    val tareas by asignaturaViewModel.tareasState.collectAsState()
    val tareasAsignatura = tareas.filter { it.asignaturaId == asignatura.asignaturaId }
    val docentes by asignaturaViewModel.docentesState.collectAsState()
    val docente = docentes.find { it.docenteId == asignatura.docenteId }
    val calificaciones by asignaturaViewModel.obtenerCalificaciones(asignatura.asignaturaId).collectAsState(initial = emptyList())

    var showAddCalificacion by remember { mutableStateOf(false) }
    var showAddTarea by remember { mutableStateOf(false) }
    
    var calificacionToEdit by remember { mutableStateOf<com.example.delfinctrl.data.model.Calificacion?>(null) }
    var tareaToEdit by remember { mutableStateOf<com.example.delfinctrl.data.model.Tarea?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(asignatura.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Créditos: ${asignatura.creditos}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            val sumCalif = calificaciones.sumOf { it.calif }
                            val acreditada = sumCalif >= 70
                            
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (acreditada) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (acreditada) "Acreditada ($sumCalif)" else "No Acreditada ($sumCalif)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (docente != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Profesor: ${docente.nombre} ${docente.apellidos}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("Email: ${docente.email}", style = MaterialTheme.typography.bodyMedium)
                                Text("Cubículo: ${docente.cubiculo}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Text("Profesor: Sin asignar", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showAddCalificacion = true }, modifier = Modifier.weight(1f)) {
                            Text("Añadir Calificación")
                        }
                        Button(onClick = { showAddTarea = true }, modifier = Modifier.weight(1f)) {
                            Text("Añadir Tarea")
                        }
                    }
                }
            }

            item {
                Text(
                    "Calificaciones", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            if (calificaciones.isEmpty()) {
                item { Text("No hay calificaciones registradas.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 16.dp)) }
            } else {
                items(calificaciones) { calif ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val dateStr = sdf.format(Date(calif.fecha))
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Nota: ${calif.calif}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text(calif.nota, style = MaterialTheme.typography.bodyMedium)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(dateStr, style = MaterialTheme.typography.bodySmall)
                                Row {
                                    IconButton(onClick = { 
                                        calificacionToEdit = calif
                                        showAddCalificacion = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { asignaturaViewModel.eliminarCalificacion(calif) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Tareas", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.primary, 
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            if (tareasAsignatura.isEmpty()) {
                item { Text("No hay tareas registradas.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 16.dp)) }
            } else {
                items(tareasAsignatura) { tarea ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tarea.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                Text(if (tarea.estado) "Completada" else "Pendiente", color = if (tarea.estado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                            Row {
                                IconButton(onClick = { 
                                    tareaToEdit = tarea
                                    showAddTarea = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { asignaturaViewModel.eliminarTarea(tarea, context) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    if (showAddCalificacion) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { 
            showAddCalificacion = false
            calificacionToEdit = null
        }, sheetState = sheetState) {
            AgregarCalificacionForm(asignatura.asignaturaId, asignaturaViewModel, calificacionToEdit) { 
                showAddCalificacion = false
                calificacionToEdit = null
            }
        }
    }

    if (showAddTarea) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { 
            showAddTarea = false
            tareaToEdit = null
        }, sheetState = sheetState) {
            AgregarTareaDirectaForm(asignatura.asignaturaId, asignaturaViewModel, tareaToEdit) { 
                showAddTarea = false
                tareaToEdit = null
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCalificacionForm(
    asignaturaId: Int,
    asignaturaViewModel: AsignaturaViewModel,
    calificacionToEdit: com.example.delfinctrl.data.model.Calificacion? = null,
    onDismiss: () -> Unit
) {
    var nota by remember(calificacionToEdit) { mutableStateOf(calificacionToEdit?.nota ?: "") }
    var califStr by remember(calificacionToEdit) { mutableStateOf(calificacionToEdit?.calif?.toString() ?: "") }
    
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calificacionToEdit?.fecha ?: System.currentTimeMillis())
    var showDatePicker by remember { mutableStateOf(false) }

    val fechaElegida = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    val fechaStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaElegida))

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(if (calificacionToEdit == null) "Añadir Calificación" else "Editar Calificación", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        OutlinedTextField(
            value = califStr,
            onValueChange = { califStr = it },
            label = { Text("Calificación (0 - 100)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        OutlinedTextField(
            value = nota,
            onValueChange = { nota = it },
            label = { Text("Descripción (Ej: Parcial 1)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxWidth()) {
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

        Button(
            onClick = {
                val calif = califStr.toIntOrNull() ?: 0
                if (califStr.isNotBlank()) {
                    if (calificacionToEdit == null) {
                        asignaturaViewModel.guardarCalificacion(
                            com.example.delfinctrl.data.model.Calificacion(
                                asignaturaId = asignaturaId,
                                calif = calif,
                                fecha = fechaElegida,
                                nota = nota.trim()
                            )
                        )
                    } else {
                        asignaturaViewModel.actualizarCalificacion(
                            calificacionToEdit.copy(
                                calif = calif,
                                fecha = fechaElegida,
                                nota = nota.trim()
                            )
                        )
                    }
                    onDismiss()
                }
            },
            enabled = califStr.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (calificacionToEdit == null) "Guardar Calificación" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaDirectaForm(
    asignaturaId: Int,
    asignaturaViewModel: AsignaturaViewModel,
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

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(if (tareaToEdit == null) "Añadir Tarea" else "Editar Tarea", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

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

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = fechaStr,
                onValueChange = {},
                label = { Text("Fecha de Entrega") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
            )
            Box(modifier = Modifier.matchParentSize().clickable { showDatePicker = true })
        }

        Button(
            onClick = {
                if (titulo.isNotBlank()) {
                    if (tareaToEdit == null) {
                        asignaturaViewModel.guardarTarea(
                            titulo = titulo.trim(),
                            fecha = fechaElegida,
                            nota = nota.trim(),
                            asignaturaId = asignaturaId,
                            context = context
                        )
                    } else {
                        asignaturaViewModel.actualizarTarea(
                            tareaToEdit.copy(
                                titulo = titulo.trim(),
                                fechaDatetime = fechaElegida,
                                nota = nota.trim()
                            ),
                            context = context
                        )
                    }
                    onDismiss()
                }
            },
            enabled = titulo.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (tareaToEdit == null) "Guardar Tarea" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
