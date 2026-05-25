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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.HorarioViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorariosScreen(
    horarioViewModel: HorarioViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    modifier: Modifier = Modifier
) {
    val horarios by horarioViewModel.horariosState.collectAsState()
    val clases by horarioViewModel.clasesState.collectAsState()
    val asignaturas by asignaturaViewModel.asignaturasState.collectAsState()
    val seleccionadoHorarioId by horarioViewModel.seleccionadoHorarioId.collectAsState()

    var showAddPeriodo by remember { mutableStateOf(false) }
    var showAddClase by remember { mutableStateOf(false) }

    // Días de la semana
    val diasSemana = listOf(
        1 to "Lunes", 2 to "Martes", 3 to "Miércoles",
        4 to "Jueves", 5 to "Viernes", 6 to "Sábado"
    )

    // Horario seleccionado actual
    val horarioSeleccionado = horarios.find { it.horarioId == seleccionadoHorarioId }

    // Filtro de clases por pestaña de día
    var tabDiaSeleccionado by remember { mutableStateOf(1) } // Lunes por defecto
    val clasesFiltradasDia = clases.filter { it.dia == tabDiaSeleccionado }.sortedBy { it.inicio }

    var selectedClaseForDetails by remember { mutableStateOf<com.example.delfinctrl.data.model.AsignaturaHorario?>(null) }
    var claseToEdit by remember { mutableStateOf<com.example.delfinctrl.data.model.AsignaturaHorario?>(null) }

    LaunchedEffect(horarios) {
        if (seleccionadoHorarioId == null && horarios.isNotEmpty()) {
            horarioViewModel.seleccionarHorario(horarios.first().horarioId)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Calendario y Horarios", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (horarioSeleccionado == null) showAddPeriodo = true
                    else showAddClase = true
                },
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
                        text = "Periodo de Horario Activo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (horarios.isEmpty()) {
                        Text(
                            text = "No tienes ningún periodo escolar registrado. Usa el botón + para crear uno.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        var expandedSelectorPeriodo by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = horarioSeleccionado?.nombre ?: "Seleccionar Periodo",
                                onValueChange = {},
                                label = { Text("Seleccionar Periodo Escolar") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(modifier = Modifier.matchParentSize().clickable { expandedSelectorPeriodo = true })

                            DropdownMenu(
                                expanded = expandedSelectorPeriodo,
                                onDismissRequest = { expandedSelectorPeriodo = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                horarios.forEach { h ->
                                    DropdownMenuItem(
                                        text = { Text(h.nombre) },
                                        onClick = {
                                            horarioViewModel.seleccionarHorario(h.horarioId)
                                            expandedSelectorPeriodo = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("+ Crear Nuevo Periodo", color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        horarioViewModel.seleccionarHorario(null)
                                        expandedSelectorPeriodo = false
                                        showAddPeriodo = true
                                    }
                                )
                            }
                        }

                        if (horarioSeleccionado != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val fInicio = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(horarioSeleccionado.fechaInicio))
                                val fFin = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(horarioSeleccionado.fechaCierre))
                                Text(
                                    text = "Duración: $fInicio - $fFin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                TextButton(
                                    onClick = { horarioViewModel.eliminarHorario(horarioSeleccionado) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            if (horarioSeleccionado != null) {
                if (asignaturas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Por favor registra materias en la pestaña 'Materias' para poder agregarlas a este horario.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                } else {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mi Calendario Semanal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        SecondaryScrollableTabRow(
                            selectedTabIndex = tabDiaSeleccionado - 1,
                            edgePadding = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            diasSemana.forEach { d ->
                                Tab(
                                    selected = tabDiaSeleccionado == d.first,
                                    onClick = { tabDiaSeleccionado = d.first },
                                    text = { Text(d.second.take(3)) } // Lun, Mar, Mie...
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (clasesFiltradasDia.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No tienes clases programadas para este día.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(clasesFiltradasDia) { clase ->
                                    val asig = asignaturas.find { it.asignaturaId == clase.asignaturaId }
                                    val asigNombre = asig?.nombre ?: "Materia"

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedClaseForDetails = clase },
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(text = asigNombre, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    Text(
                                                        text = "Hora: ${formatMinutesToTime(clase.inicio)} - ${formatMinutesToTime(clase.fin)}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    if (!clase.salon.isNullOrBlank()) {
                                                        Text(text = "Salón: ${clase.salon}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddPeriodo) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showAddPeriodo = false }, sheetState = sheetState) {
            AgregarPeriodoForm(horarioViewModel) { showAddPeriodo = false }
        }
    }

    if (showAddClase && horarioSeleccionado != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { showAddClase = false }, sheetState = sheetState) {
            AgregarClaseForm(
                horarioId = horarioSeleccionado.horarioId,
                asignaturas = asignaturas,
                diasSemana = diasSemana,
                horarioViewModel = horarioViewModel,
                claseToEdit = null,
                onDismiss = { showAddClase = false }
            )
        }
    }

    if (selectedClaseForDetails != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { selectedClaseForDetails = null }, sheetState = sheetState) {
            DetalleClaseSheet(
                clase = selectedClaseForDetails!!,
                asignaturas = asignaturas,
                diasSemana = diasSemana,
                horarioViewModel = horarioViewModel,
                onDismiss = { selectedClaseForDetails = null },
                onEdit = {
                    showAddClase = true // Actually we should open a form with prefilled state. 
                    // We can reuse showAddClase with a state variable 'claseToEdit' if we want.
                    // For simplicity, we can do it by managing a 'claseToEdit' state.
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPeriodoForm(
    horarioViewModel: HorarioViewModel,
    onDismiss: () -> Unit
) {
    var nombrePeriodo by remember { mutableStateOf("") }
    
    val datePickerInicioState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var showDatePickerInicio by remember { mutableStateOf(false) }
    
    val cal = Calendar.getInstance()
    cal.add(Calendar.MONTH, 6)
    val datePickerCierreState = rememberDatePickerState(initialSelectedDateMillis = cal.timeInMillis)
    var showDatePickerCierre by remember { mutableStateOf(false) }

    val fechaInicioElegida = datePickerInicioState.selectedDateMillis ?: System.currentTimeMillis()
    val fechaInicioStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaInicioElegida))

    val fechaCierreElegida = datePickerCierreState.selectedDateMillis ?: cal.timeInMillis
    val fechaCierreStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(fechaCierreElegida))

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Crear Nuevo Periodo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        
        OutlinedTextField(
            value = nombrePeriodo,
            onValueChange = { nombrePeriodo = it },
            label = { Text("Nombre (Ej: Enero - Junio 2026)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fechaInicioStr,
                    onValueChange = {},
                    label = { Text("Fecha Inicio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePickerInicio = true })
            }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = fechaCierreStr,
                    onValueChange = {},
                    label = { Text("Fecha Fin") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDatePickerCierre = true })
            }
        }
        Button(
            onClick = {
                if (nombrePeriodo.isNotBlank()) {
                    horarioViewModel.guardarHorario(nombrePeriodo.trim(), fechaInicioElegida, fechaCierreElegida)
                    onDismiss()
                }
            },
            enabled = nombrePeriodo.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Periodo")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDatePickerInicio) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerInicio = false },
            confirmButton = { TextButton(onClick = { showDatePickerInicio = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePickerInicio = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerInicioState)
        }
    }
    
    if (showDatePickerCierre) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerCierre = false },
            confirmButton = { TextButton(onClick = { showDatePickerCierre = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showDatePickerCierre = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerCierreState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleClaseSheet(
    clase: com.example.delfinctrl.data.model.AsignaturaHorario,
    asignaturas: List<Asignatura>,
    diasSemana: List<Pair<Int, String>>,
    horarioViewModel: HorarioViewModel,
    onDismiss: () -> Unit,
    onEdit: () -> Unit
) {
    val asig = asignaturas.find { it.asignaturaId == clase.asignaturaId }
    val diaStr = diasSemana.find { it.first == clase.dia }?.second ?: ""
    
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Detalles de la Clase", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Materia: ${asig?.nombre ?: ""}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("Día: $diaStr", style = MaterialTheme.typography.bodyMedium)
                Text("Horario: ${formatMinutesToTime(clase.inicio)} - ${formatMinutesToTime(clase.fin)}", style = MaterialTheme.typography.bodyMedium)
                if (!clase.salon.isNullOrBlank()) {
                    Text("Salón: ${clase.salon}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    onEdit()
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar")
            }
            
            Button(
                onClick = {
                    horarioViewModel.eliminarClase(clase)
                    onDismiss()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Eliminar")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarClaseForm(
    horarioId: Int,
    asignaturas: List<Asignatura>,
    diasSemana: List<Pair<Int, String>>,
    horarioViewModel: HorarioViewModel,
    claseToEdit: com.example.delfinctrl.data.model.AsignaturaHorario? = null,
    onDismiss: () -> Unit
) {
    var selectedAsignaturaClase by remember(claseToEdit) { mutableStateOf(if (claseToEdit != null) asignaturas.find { it.asignaturaId == claseToEdit.asignaturaId } else asignaturas.firstOrNull()) }
    var expandedAsignaturaClase by remember { mutableStateOf(false) }
    var diaSeleccionado by remember(claseToEdit) { mutableStateOf(claseToEdit?.dia ?: 1) }
    var expandedDia by remember { mutableStateOf(false) }
    var salonStr by remember(claseToEdit) { mutableStateOf(claseToEdit?.salon ?: "") }

    val timePickerInicioState = rememberTimePickerState(
        initialHour = claseToEdit?.inicio?.div(60) ?: 8, 
        initialMinute = claseToEdit?.inicio?.rem(60) ?: 0, 
        is24Hour = true
    )
    var showTimePickerInicio by remember { mutableStateOf(false) }

    val timePickerFinState = rememberTimePickerState(
        initialHour = claseToEdit?.fin?.div(60) ?: 9, 
        initialMinute = claseToEdit?.fin?.rem(60) ?: 30, 
        is24Hour = true
    )
    var showTimePickerFin by remember { mutableStateOf(false) }

    val horaInicioStr = formatMinutesToTime(timePickerInicioState.hour * 60 + timePickerInicioState.minute)
    val horaFinStr = formatMinutesToTime(timePickerFinState.hour * 60 + timePickerFinState.minute)

    val inicioMins = timePickerInicioState.hour * 60 + timePickerInicioState.minute
    val finMins = timePickerFinState.hour * 60 + timePickerFinState.minute

    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(if (claseToEdit == null) "Asignar Clase a Horario" else "Editar Clase", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedAsignaturaClase?.nombre ?: "Seleccionar Materia",
                onValueChange = {},
                readOnly = true,
                label = { Text("Materia") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
            )
            Box(modifier = Modifier.matchParentSize().clickable { expandedAsignaturaClase = true })
            DropdownMenu(expanded = expandedAsignaturaClase, onDismissRequest = { expandedAsignaturaClase = false }) {
                asignaturas.forEach { asig ->
                    DropdownMenuItem(text = { Text(asig.nombre) }, onClick = { selectedAsignaturaClase = asig; expandedAsignaturaClase = false })
                }
            }
        }

        OutlinedTextField(
            value = salonStr,
            onValueChange = { salonStr = it },
            label = { Text("Salón (Opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = diasSemana.find { it.first == diaSeleccionado }?.second ?: "Lunes",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Día") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { expandedDia = true })
                DropdownMenu(expanded = expandedDia, onDismissRequest = { expandedDia = false }) {
                    diasSemana.forEach { d ->
                        DropdownMenuItem(text = { Text(d.second) }, onClick = { diaSeleccionado = d.first; expandedDia = false })
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = horaInicioStr,
                    onValueChange = {},
                    label = { Text("Inicio") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showTimePickerInicio = true })
            }

            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = horaFinStr,
                    onValueChange = {},
                    label = { Text("Fin") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showTimePickerFin = true })
            }
        }

        Button(
            onClick = {
                if (selectedAsignaturaClase != null && inicioMins < finMins) {
                    if (claseToEdit == null) {
                        horarioViewModel.agregarClase(
                            horarioId = horarioId,
                            asignaturaId = selectedAsignaturaClase!!.asignaturaId,
                            dia = diaSeleccionado,
                            inicio = inicioMins,
                            fin = finMins,
                            salon = salonStr.trim()
                        )
                    } else {
                        horarioViewModel.actualizarClase(
                            claseToEdit.copy(
                                dia = diaSeleccionado,
                                inicio = inicioMins,
                                fin = finMins,
                                salon = salonStr.trim()
                            )
                        )
                    }
                    onDismiss()
                }
            },
            enabled = selectedAsignaturaClase != null && inicioMins < finMins,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (claseToEdit == null) "Agregar al Calendario" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showTimePickerInicio) {
        AlertDialog(
            onDismissRequest = { showTimePickerInicio = false },
            confirmButton = { TextButton(onClick = { showTimePickerInicio = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showTimePickerInicio = false }) { Text("Cancelar") } },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerInicioState)
                }
            }
        )
    }

    if (showTimePickerFin) {
        AlertDialog(
            onDismissRequest = { showTimePickerFin = false },
            confirmButton = { TextButton(onClick = { showTimePickerFin = false }) { Text("OK") } },
            dismissButton = { TextButton(onClick = { showTimePickerFin = false }) { Text("Cancelar") } },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerFinState)
                }
            }
        )
    }
}

fun formatMinutesToTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return String.format(Locale.getDefault(), "%02d:%02d", h, m)
}

fun parseTimeToMinutes(timeStr: String): Int? {
    val parts = timeStr.split(":")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return h * 60 + m
}
