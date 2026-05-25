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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.AsignaturaHorario
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

    // Formulario de Periodo
    var nombrePeriodo by remember { mutableStateOf("") }
    var fechaInicioStr by remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        mutableStateOf(sdf.format(Date()))
    }
    var fechaCierreStr by remember {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Prefijar 6 meses en adelante
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 6)
        mutableStateOf(sdf.format(cal.time))
    }

    // Formulario de asignación de clase
    var selectedAsignaturaClase by remember { mutableStateOf<Asignatura?>(null) }
    var expandedAsignaturaClase by remember { mutableStateOf(false) }
    var diaSeleccionado by remember { mutableStateOf(1) } // 1 = Lun, 2 = Mar, etc.
    var expandedDia by remember { mutableStateOf(false) }
    var horaInicioStr by remember { mutableStateOf("08:00") }
    var horaFinStr by remember { mutableStateOf("09:30") }

    // Días de la semana
    val diasSemana = listOf(
        1 to "Lunes",
        2 to "Martes",
        3 to "Miércoles",
        4 to "Jueves",
        5 to "Viernes",
        6 to "Sábado"
    )

    // Horario seleccionado actual
    val horarioSeleccionado = horarios.find { it.horarioId == seleccionadoHorarioId }

    // Al cargar los horarios, seleccionar el primero si no hay ninguno seleccionado
    LaunchedEffect(horarios) {
        if (seleccionadoHorarioId == null && horarios.isNotEmpty()) {
            horarioViewModel.seleccionarHorario(horarios.first().horarioId)
        }
    }

    // Al cargar asignaturas, preseleccionar la primera en el formulario de clases
    LaunchedEffect(asignaturas) {
        if (selectedAsignaturaClase == null && asignaturas.isNotEmpty()) {
            selectedAsignaturaClase = asignaturas.first()
        }
    }

    // Filtro de clases por pestaña de día
    var tabDiaSeleccionado by remember { mutableStateOf(1) } // Lunes por defecto
    val clasesFiltradasDia = clases.filter { it.dia == tabDiaSeleccionado }.sortedBy { it.inicio }

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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Selector/Creación de Períodos de Horario
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
                        // Crear periodo si no existe ninguno
                        Text(
                            text = "No tienes ningún periodo escolar registrado. Crea uno a continuación:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = nombrePeriodo,
                            onValueChange = { nombrePeriodo = it },
                            label = { Text("Nombre (Ej: Enero - Junio 2026)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = fechaInicioStr,
                                onValueChange = { fechaInicioStr = it },
                                label = { Text("Fecha Inicio") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = fechaCierreStr,
                                onValueChange = { fechaCierreStr = it },
                                label = { Text("Fecha Fin") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Button(
                            onClick = {
                                if (nombrePeriodo.isNotBlank()) {
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val inicio = try { sdf.parse(fechaInicioStr)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }
                                    val fin = try { sdf.parse(fechaCierreStr)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }
                                    horarioViewModel.guardarHorario(nombrePeriodo.trim(), inicio, fin)
                                    nombrePeriodo = ""
                                }
                            },
                            enabled = nombrePeriodo.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Crear Periodo")
                        }
                    } else {
                        // Selector de periodos existentes
                        var expandedSelectorPeriodo by remember { mutableStateOf(false) }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = horarioSeleccionado?.nombre ?: "Seleccionar Periodo",
                                onValueChange = {},
                                label = { Text("Seleccionar Periodo Escolar") },
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
                                    .clickable { expandedSelectorPeriodo = true }
                            )

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
                                // Opción para permitir crear otro periodo
                                DropdownMenuItem(
                                    text = { Text("+ Crear Nuevo Periodo", color = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        horarioViewModel.seleccionarHorario(null)
                                        expandedSelectorPeriodo = false
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

            // Si hay un periodo de horario seleccionado y existen materias, permitir agregar clases y ver la cuadrícula
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
                    // Formulario de agregar clase
                    var mostrarFormClase by remember { mutableStateOf(false) }

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { mostrarFormClase = !mostrarFormClase },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Asignar Clase a Horario",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }

                            if (mostrarFormClase) {
                                Spacer(modifier = Modifier.height(12.dp))
                                // Dropdown materias
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedAsignaturaClase?.nombre ?: "Seleccionar Materia",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Materia") },
                                        modifier = Modifier.fillMaxWidth(),
                                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { expandedAsignaturaClase = true }
                                    )
                                    DropdownMenu(
                                        expanded = expandedAsignaturaClase,
                                        onDismissRequest = { expandedAsignaturaClase = false }
                                    ) {
                                        asignaturas.forEach { asig ->
                                            DropdownMenuItem(
                                                text = { Text(asig.nombre) },
                                                onClick = {
                                                    selectedAsignaturaClase = asig
                                                    expandedAsignaturaClase = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // Selector Día
                                    Box(modifier = Modifier.weight(1f)) {
                                        OutlinedTextField(
                                            value = diasSemana.find { it.first == diaSeleccionado }?.second ?: "Lunes",
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("Día") },
                                            modifier = Modifier.fillMaxWidth(),
                                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .clickable { expandedDia = true }
                                        )
                                        DropdownMenu(
                                            expanded = expandedDia,
                                            onDismissRequest = { expandedDia = false }
                                        ) {
                                            diasSemana.forEach { d ->
                                                DropdownMenuItem(
                                                    text = { Text(d.second) },
                                                    onClick = {
                                                        diaSeleccionado = d.first
                                                        expandedDia = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Hora Inicio y Fin
                                    OutlinedTextField(
                                        value = horaInicioStr,
                                        onValueChange = { horaInicioStr = it },
                                        label = { Text("Inicio (HH:mm)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    OutlinedTextField(
                                        value = horaFinStr,
                                        onValueChange = { horaFinStr = it },
                                        label = { Text("Fin (HH:mm)") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val inicioMins = parseTimeToMinutes(horaInicioStr)
                                        val finMins = parseTimeToMinutes(horaFinStr)

                                        if (selectedAsignaturaClase != null && inicioMins != null && finMins != null && inicioMins < finMins) {
                                            horarioViewModel.agregarClase(
                                                horarioId = horarioSeleccionado.horarioId,
                                                asignaturaId = selectedAsignaturaClase!!.asignaturaId,
                                                dia = diaSeleccionado,
                                                inicio = inicioMins,
                                                fin = finMins
                                            )
                                            mostrarFormClase = false
                                        }
                                    },
                                    enabled = selectedAsignaturaClase != null && horaInicioStr.isNotBlank() && horaFinStr.isNotBlank(),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Agregar al Calendario")
                                }
                            }
                        }
                    }

                    // 3. Vista de Calendario semanal en Pestañas por Día
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(clasesFiltradasDia) { clase ->
                                    val asig = asignaturas.find { it.asignaturaId == clase.asignaturaId }
                                    val asigNombre = asig?.nombre ?: "Materia"
                                    val creditos = asig?.creditos ?: 0

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = asigNombre,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    Text(
                                                        text = "Hora: ${formatMinutesToTime(clase.inicio)} - ${formatMinutesToTime(clase.fin)}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "Créditos: $creditos",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            IconButton(onClick = { horarioViewModel.eliminarClase(clase) }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Quitar clase",
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
            }
        }
    }
}

// Helpers para conversión y formateo de hora en minutos
fun formatMinutesToTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return String.format("%02d:%02d", h, m)
}

fun parseTimeToMinutes(timeStr: String): Int? {
    val parts = timeStr.split(":")
    if (parts.size != 2) return null
    val h = parts[0].toIntOrNull() ?: return null
    val m = parts[1].toIntOrNull() ?: return null
    if (h !in 0..23 || m !in 0..59) return null
    return h * 60 + m
}
