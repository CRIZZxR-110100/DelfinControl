package com.example.delfinctrl.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.delfinctrl.data.model.Estudiante
import com.example.delfinctrl.ui.viewmodels.EstudianteViewModel
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.ActividadAfiViewModel

@Composable
fun EstudianteFormulario(
    onGuardar: (Estudiante) -> Unit,
    estudianteToEdit: Estudiante? = null,
    modifier: Modifier = Modifier
) {
    var matricula by rememberSaveable { mutableStateOf(estudianteToEdit?.estudianteId ?: "") }
    var nombre by rememberSaveable { mutableStateOf(estudianteToEdit?.nombre ?: "") }
    var apellidos by rememberSaveable { mutableStateOf(estudianteToEdit?.apellidos ?: "") }
    var institucion by rememberSaveable { mutableStateOf(estudianteToEdit?.institucion ?: "") }
    var facultad by rememberSaveable { mutableStateOf(estudianteToEdit?.facultad ?: "") }
    var programaEducativo by rememberSaveable { mutableStateOf(estudianteToEdit?.programa_educativo ?: "") }

    var creditosRequeridos by rememberSaveable { mutableStateOf("120") }
    var afisRequeridas by rememberSaveable { mutableStateOf("20") }

    val formularioValido = listOf(
        matricula,
        nombre,
        apellidos,
        institucion,
        facultad,
        programaEducativo,
        creditosRequeridos,
        afisRequeridas
    ).all { it.isNotBlank() }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera Premium DelfinControl
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐬", style = MaterialTheme.typography.headlineLarge)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DelfinControl",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Completa tus datos para iniciar tu sesión local.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Campo Matrícula
                OutlinedTextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matrícula") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Apellidos
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Institución
                OutlinedTextField(
                    value = institucion,
                    onValueChange = { institucion = it },
                    label = { Text("Institución") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Facultad
                OutlinedTextField(
                    value = facultad,
                    onValueChange = { facultad = it },
                    label = { Text("Facultad") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Programa Educativo
                OutlinedTextField(
                    value = programaEducativo,
                    onValueChange = { programaEducativo = it },
                    label = { Text("Programa Educativo") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo Créditos Requeridos
                OutlinedTextField(
                    value = creditosRequeridos,
                    onValueChange = { if (it.all { char -> char.isDigit() }) creditosRequeridos = it },
                    label = { Text("Créditos Requeridos") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Campo AFIs Requeridas
                OutlinedTextField(
                    value = afisRequeridas,
                    onValueChange = { if (it.all { char -> char.isDigit() }) afisRequeridas = it },
                    label = { Text("AFIs Requeridas") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Botón Iniciar Sesión
                Button(
                    onClick = {
                        onGuardar(
                            Estudiante(
                                estudianteId = matricula.trim(),
                                nombre = nombre.trim(),
                                apellidos = apellidos.trim(),
                                institucion = institucion.trim(),
                                facultad = facultad.trim(),
                                programa_educativo = programaEducativo.trim(),
                                creditos_requeridos = if (estudianteToEdit == null) creditosRequeridos.toIntOrNull() ?: 120 else estudianteToEdit.creditos_requeridos,
                                afis_requeridas = if (estudianteToEdit == null) afisRequeridas.toIntOrNull() ?: 20 else estudianteToEdit.afis_requeridas
                            )
                        )
                    },
                    enabled = formularioValido,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (estudianteToEdit == null) "Registrar Perfil" else "Guardar Cambios",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudianteDetalle(
    estudiante: Estudiante,
    totalCreditos: Int,
    totalAfi: Int,
    onActualizarLimites: (Int, Int) -> Unit,
    onActualizarPerfil: (Estudiante) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditLimitsBottomSheet by remember { mutableStateOf(false) }
    var showEditProfileBottomSheet by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Tarjeta de Progreso Académico (Créditos y AFIs)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progreso Académico",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        TextButton(onClick = { showEditLimitsBottomSheet = true }) {
                            Text("Editar límites")
                        }
                    }

                    // Barra de Progreso de Créditos
                    val reqCreditos = estudiante.creditos_requeridos ?: 120
                    val pctCreditos = if (reqCreditos > 0) (totalCreditos.toFloat() / reqCreditos.toFloat()).coerceIn(0f, 1f) else 0f
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Créditos Aprobados", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = "$totalCreditos / $reqCreditos (${(pctCreditos * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { pctCreditos },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    }

                    // Barra de Progreso de Horas AFI
                    val reqAfi = estudiante.afis_requeridas ?: 20
                    val pctAfi = if (reqAfi > 0) (totalAfi.toFloat() / reqAfi.toFloat()).coerceIn(0f, 1f) else 0f
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Horas AFI Registradas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                text = "$totalAfi / $reqAfi (${(pctAfi * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { pctAfi },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Datos Personales
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileItem(label = "Nombre Completo", value = "${estudiante.nombre} ${estudiante.apellidos}", icon = Icons.Default.Person)
                    ProfileItem(label = "Matrícula", value = estudiante.estudianteId, icon = Icons.Default.Person)
                    ProfileItem(label = "Institución", value = estudiante.institucion, icon = Icons.Default.Home)
                    ProfileItem(label = "Facultad", value = estudiante.facultad, icon = Icons.Default.Home)
                    ProfileItem(label = "Programa Educativo", value = estudiante.programa_educativo, icon = Icons.Default.Info)
                }
            }

            Button(
                onClick = { showEditProfileBottomSheet = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Editar Perfil", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showEditLimitsBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var newCreditos by remember { mutableStateOf((estudiante.creditos_requeridos ?: 120).toString()) }
        var newAfis by remember { mutableStateOf((estudiante.afis_requeridas ?: 20).toString()) }

        ModalBottomSheet(
            onDismissRequest = { showEditLimitsBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Editar Límites",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                OutlinedTextField(
                    value = newCreditos,
                    onValueChange = { if (it.all { char -> char.isDigit() }) newCreditos = it },
                    label = { Text("Créditos Requeridos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = newAfis,
                    onValueChange = { if (it.all { char -> char.isDigit() }) newAfis = it },
                    label = { Text("AFIs Requeridas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val creds = newCreditos.toIntOrNull() ?: 120
                        val afis = newAfis.toIntOrNull() ?: 20
                        onActualizarLimites(creds, afis)
                        showEditLimitsBottomSheet = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showEditProfileBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showEditProfileBottomSheet = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.95f)
        ) {
            EstudianteFormulario(
                onGuardar = { updatedEstudiante ->
                    onActualizarPerfil(updatedEstudiante)
                    showEditProfileBottomSheet = false
                },
                estudianteToEdit = estudiante
            )
        }
    }
}

@Composable
fun ProfileItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ProfileScreen(
    estudianteViewModel: EstudianteViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    actividadAfiViewModel: ActividadAfiViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by estudianteViewModel.uiState.collectAsState()
    val totalCreditos by asignaturaViewModel.creditosTotalesState.collectAsState()
    val totalAfi by actividadAfiViewModel.horasAfiTotalesState.collectAsState()

    if (uiState is com.example.delfinctrl.ui.viewmodels.EstudianteUiState.Success) {
        val estudiante = (uiState as com.example.delfinctrl.ui.viewmodels.EstudianteUiState.Success).estudiante
        estudiante?.let { datosEstudiante ->
            EstudianteDetalle(
                estudiante = datosEstudiante,
                totalCreditos = totalCreditos,
                totalAfi = totalAfi,
                onActualizarLimites = { creditos, afis ->
                    estudianteViewModel.actualizarEstudiante(
                        datosEstudiante.copy(
                            creditos_requeridos = creditos,
                            afis_requeridas = afis
                        )
                    )
                },
                onActualizarPerfil = { updatedEstudiante ->
                    estudianteViewModel.actualizarEstudiante(updatedEstudiante)
                },
                modifier = modifier
            )
        }
    }
}