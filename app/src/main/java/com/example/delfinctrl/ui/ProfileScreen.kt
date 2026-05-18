package com.example.delfinctrl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Estudiante
import com.example.delfinctrl.ui.viewmodels.EstudianteViewModel

@Composable
fun EstudianteFormulario(
    onGuardar: (Estudiante) -> Unit,
    modifier: Modifier = Modifier
) {
    var matricula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }
    var facultad by remember { mutableStateOf("") }
    var prog_edu by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = matricula,
            onValueChange = { matricula = it },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = apellidos,
            onValueChange = { apellidos = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = institucion,
            onValueChange = { institucion = it },
            label = { Text("Institución") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = facultad,
            onValueChange = { facultad = it },
            label = { Text("Facultad") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = prog_edu,
            onValueChange = { prog_edu = it },
            label = { Text("Programa Educativo") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                onGuardar(
                    Estudiante(
                        estudianteId = matricula,
                        nombre = nombre,
                        apellidos = apellidos,
                        institucion = institucion,
                        facultad = facultad,
                        programa_educativo = prog_edu
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}

@Composable
fun EstudianteDetalle(estudiante: Estudiante, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Mi Perfil", style = MaterialTheme.typography.headlineMedium)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Nombre: ${estudiante.nombre} ${estudiante.apellidos}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Matrícula: ${estudiante.estudianteId}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Institución: ${estudiante.institucion}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Facultad: ${estudiante.facultad}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Programa Educativo: ${estudiante.programa_educativo}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ProfileScreen(viewModel: EstudianteViewModel, modifier: Modifier = Modifier) {
    val estudiante by viewModel.estudianteState.collectAsState()

    estudiante?.let { datosEstudiante ->
        EstudianteDetalle(
            estudiante = datosEstudiante,
            modifier = modifier
        )
    }
}