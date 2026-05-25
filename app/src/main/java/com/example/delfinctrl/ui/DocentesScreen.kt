package com.example.delfinctrl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocentesScreen(
    asignaturaViewModel: AsignaturaViewModel,
    modifier: Modifier = Modifier
) {
    val docentes by asignaturaViewModel.docentesState.collectAsState()
    var showAddDocente by remember { mutableStateOf(false) }
    var docenteToEdit by remember { mutableStateOf<com.example.delfinctrl.data.model.Docente?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Directorio de Profesores", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDocente = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Profesor")
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
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                            Row {
                                IconButton(onClick = { 
                                    docenteToEdit = docente
                                    showAddDocente = true
                                }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { asignaturaViewModel.eliminarDocente(docente) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
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

    if (showAddDocente) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(onDismissRequest = { 
            showAddDocente = false
            docenteToEdit = null
        }, sheetState = sheetState) {
            AgregarDocenteForm(asignaturaViewModel, docenteToEdit) { 
                showAddDocente = false
                docenteToEdit = null
            }
        }
    }
}

@Composable
fun AgregarDocenteForm(
    asignaturaViewModel: AsignaturaViewModel,
    docenteToEdit: com.example.delfinctrl.data.model.Docente? = null,
    onDismiss: () -> Unit
) {
    var nombreDocente by remember(docenteToEdit) { mutableStateOf(docenteToEdit?.nombre ?: "") }
    var apellidosDocente by remember(docenteToEdit) { mutableStateOf(docenteToEdit?.apellidos ?: "") }
    var emailDocente by remember(docenteToEdit) { mutableStateOf(docenteToEdit?.email ?: "") }
    var cubiculoDocente by remember(docenteToEdit) { mutableStateOf(docenteToEdit?.cubiculo ?: "") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            if (docenteToEdit == null) "Registrar Nuevo Profesor" else "Editar Profesor",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = nombreDocente,
            onValueChange = { nombreDocente = it },
            label = { Text("Nombre(s)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = apellidosDocente,
            onValueChange = { apellidosDocente = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = emailDocente,
            onValueChange = { emailDocente = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = cubiculoDocente,
            onValueChange = { cubiculoDocente = it },
            label = { Text("Cubículo / Oficina") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                if (nombreDocente.isNotBlank() && apellidosDocente.isNotBlank()) {
                    if (docenteToEdit == null) {
                        asignaturaViewModel.guardarDocente(
                            nombreDocente.trim(),
                            apellidosDocente.trim(),
                            emailDocente.trim(),
                            cubiculoDocente.trim()
                        )
                    } else {
                        asignaturaViewModel.actualizarDocente(
                            docenteToEdit.copy(
                                nombre = nombreDocente.trim(),
                                apellidos = apellidosDocente.trim(),
                                email = emailDocente.trim(),
                                cubiculo = cubiculoDocente.trim()
                            )
                        )
                    }
                    onDismiss()
                }
            },
            enabled = nombreDocente.isNotBlank() && apellidosDocente.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (docenteToEdit == null) "Registrar Docente" else "Guardar Cambios")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
