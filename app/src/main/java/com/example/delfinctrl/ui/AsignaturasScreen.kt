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
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.DocenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsignaturasScreen(
    asignaturaViewModel: AsignaturaViewModel,
    docenteViewModel: DocenteViewModel,
    modifier: Modifier = Modifier
) {
    val asignaturas by asignaturaViewModel.asignaturasState.collectAsState()
    val totalCreditos by asignaturaViewModel.creditosTotalesState.collectAsState()
    val docentes by docenteViewModel.docentesState.collectAsState()

    var subTabSeleccionada by remember { mutableStateOf(0) } // 0 = Materias, 1 = Profesores

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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pestañas secundarias: Materias / Profesores
            SecondaryTabRow(selectedTabIndex = subTabSeleccionada) {
                Tab(
                    selected = subTabSeleccionada == 0,
                    onClick = { subTabSeleccionada = 0 },
                    text = { Text("Materias") }
                )
                Tab(
                    selected = subTabSeleccionada == 1,
                    onClick = { subTabSeleccionada = 1 },
                    text = { Text("Profesores") }
                )
            }

            if (subTabSeleccionada == 0) {
                // PESTAÑA: MATERIAS
                var nombre by remember { mutableStateOf("") }
                var creditosStr by remember { mutableStateOf("") }
                var selectedPrevia by remember { mutableStateOf<Asignatura?>(null) }
                var expandedPrevia by remember { mutableStateOf(false) }
                var selectedDocente by remember { mutableStateOf<Docente?>(null) }
                var expandedDocente by remember { mutableStateOf(false) }

                // Formulario de agregar materia
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
                            text = "Agregar Nueva Materia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre de la Materia") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = creditosStr,
                                onValueChange = { creditosStr = it },
                                label = { Text("Créditos") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Selector de Prerrequisito
                            Box(modifier = Modifier.weight(1.5f)) {
                                OutlinedTextField(
                                    value = selectedPrevia?.nombre ?: "Ninguna",
                                    onValueChange = {},
                                    label = { Text("Materia Previa") },
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { expandedPrevia = true }
                                )

                                DropdownMenu(
                                    expanded = expandedPrevia,
                                    onDismissRequest = { expandedPrevia = false },
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Ninguna") },
                                        onClick = {
                                            selectedPrevia = null
                                            expandedPrevia = false
                                        }
                                    )
                                    asignaturas.forEach { asignatura ->
                                        DropdownMenuItem(
                                            text = { Text(asignatura.nombre) },
                                            onClick = {
                                                selectedPrevia = asignatura
                                                expandedPrevia = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Selector de Docente que la imparte
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = selectedDocente?.let { "${it.nombre} ${it.apellidos}" } ?: "Sin asignar",
                                onValueChange = {},
                                label = { Text("Docente / Profesor") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { expandedDocente = true }
                            )

                            DropdownMenu(
                                expanded = expandedDocente,
                                onDismissRequest = { expandedDocente = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sin asignar") },
                                    onClick = {
                                        selectedDocente = null
                                        expandedDocente = false
                                    }
                                )
                                docentes.forEach { docente ->
                                    DropdownMenuItem(
                                        text = { Text("${docente.nombre} ${docente.apellidos} (Cubículo: ${docente.cubiculo})") },
                                        onClick = {
                                            selectedDocente = docente
                                            expandedDocente = false
                                        }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val creditos = creditosStr.toIntOrNull() ?: 0
                                if (nombre.isNotBlank()) {
                                    asignaturaViewModel.guardarAsignatura(
                                        nombre = nombre.trim(),
                                        creditos = creditos,
                                        previaId = selectedPrevia?.asignaturaId,
                                        docenteId = selectedDocente?.docenteId
                                    )
                                    nombre = ""
                                    creditosStr = ""
                                    selectedPrevia = null
                                    selectedDocente = null
                                }
                            },
                            enabled = nombre.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Registrar Materia")
                        }
                    }
                }

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
                            text = "Créditos Totales Registrados:",
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

                // Lista de materias
                Text(
                    text = "Materias Registradas (${asignaturas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(asignaturas) { asignatura ->
                        val previaNombre = asignaturas.find { it.asignaturaId == asignatura.previa }?.nombre ?: "Ninguna"
                        val docenteAsignado = docentes.find { it.docenteId == asignatura.docenteId }
                        val docenteNombre = docenteAsignado?.let { "${it.nombre} ${it.apellidos}" } ?: "Sin asignar"

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = asignatura.nombre,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Profesor: $docenteNombre",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (docenteAsignado != null) {
                                        Text(
                                            text = "Contacto: ${docenteAsignado.email} | Cubículo: ${docenteAsignado.cubiculo}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text(
                                            text = "Créditos: ${asignatura.creditos}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Previa: $previaNombre",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(onClick = { asignaturaViewModel.eliminarAsignatura(asignatura) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar materia",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // PESTAÑA: PROFESORES
                var nombreDocente by remember { mutableStateOf("") }
                var apellidosDocente by remember { mutableStateOf("") }
                var emailDocente by remember { mutableStateOf("") }
                var cubiculoDocente by remember { mutableStateOf("") }

                // Formulario de agregar docente
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
                            text = "Registrar Nuevo Profesor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = nombreDocente,
                                onValueChange = { nombreDocente = it },
                                label = { Text("Nombre(s)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = apellidosDocente,
                                onValueChange = { apellidosDocente = it },
                                label = { Text("Apellidos") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = emailDocente,
                            onValueChange = { emailDocente = it },
                            label = { Text("Correo Electrónico") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = cubiculoDocente,
                            onValueChange = { cubiculoDocente = it },
                            label = { Text("Cubículo / Oficina") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (nombreDocente.isNotBlank() && apellidosDocente.isNotBlank()) {
                                    docenteViewModel.guardarDocente(
                                        nombre = nombreDocente.trim(),
                                        apellidos = apellidosDocente.trim(),
                                        email = emailDocente.trim(),
                                        cubiculo = cubiculoDocente.trim()
                                    )
                                    nombreDocente = ""
                                    apellidosDocente = ""
                                    emailDocente = ""
                                    cubiculoDocente = ""
                                }
                            },
                            enabled = nombreDocente.isNotBlank() && apellidosDocente.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Registrar Docente")
                        }
                    }
                }

                // Lista de Profesores
                Text(
                    text = "Profesores Registrados (${docentes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(docentes) { docente ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${docente.nombre} ${docente.apellidos}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Email: ${docente.email}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Cubículo: ${docente.cubiculo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                IconButton(onClick = { docenteViewModel.eliminarDocente(docente) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar docente",
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
