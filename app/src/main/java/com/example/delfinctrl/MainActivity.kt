package com.example.delfinctrl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.delfinctrl.data.database.DelfinDB
import com.example.delfinctrl.data.repository.EstudianteRepository
import com.example.delfinctrl.data.repository.AsignaturaRepository
import com.example.delfinctrl.data.repository.TareaRepository
import com.example.delfinctrl.data.repository.HorarioRepository
import com.example.delfinctrl.data.repository.DocenteRepository
import com.example.delfinctrl.data.repository.ActividadAfiRepository
import com.example.delfinctrl.ui.theme.DelfinControlTheme
import com.example.delfinctrl.ui.EstudianteFormulario
import com.example.delfinctrl.ui.ProfileScreen
import com.example.delfinctrl.ui.AsignaturasScreen
import com.example.delfinctrl.ui.TareasScreen
import com.example.delfinctrl.ui.HorariosScreen
import com.example.delfinctrl.ui.AfisScreen
import com.example.delfinctrl.ui.viewmodels.EstudianteViewModel
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.TareaViewModel
import com.example.delfinctrl.ui.viewmodels.HorarioViewModel
import com.example.delfinctrl.ui.viewmodels.DocenteViewModel
import com.example.delfinctrl.ui.viewmodels.ActividadAfiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelfinControlTheme {
                RootScreen()
            }
        }
    }
}

@Composable
fun RootScreen() {
    val context = LocalContext.current

    val database = DelfinDB.getDatabase(context)
    val repoEstudiante = remember { EstudianteRepository(database.estudianteDAO()) }
    val repoAsignatura = remember { AsignaturaRepository(database.asignaturaDAO()) }
    val repoTarea = remember { TareaRepository(database.tareaDAO()) }
    val repoHorario = remember { HorarioRepository(database.horarioDAO(), database.asignaturaHorarioDAO()) }
    val repoDocente = remember { DocenteRepository(database.docenteDAO()) }
    val repoActividadAfi = remember { ActividadAfiRepository(database.actividadAfiDAO()) }

    val estudianteViewModel: EstudianteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EstudianteViewModel(repoEstudiante) as T
            }
        }
    )

    val asignaturaViewModel: AsignaturaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AsignaturaViewModel(repoAsignatura) as T
            }
        }
    )

    val tareaViewModel: TareaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TareaViewModel(repoTarea) as T
            }
        }
    )

    val horarioViewModel: HorarioViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HorarioViewModel(repoHorario) as T
            }
        }
    )

    val docenteViewModel: DocenteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DocenteViewModel(repoDocente) as T
            }
        }
    )

    val actividadAfiViewModel: ActividadAfiViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActividadAfiViewModel(repoActividadAfi) as T
            }
        }
    )

    val estudiante by estudianteViewModel.estudianteState.collectAsState()

    if (estudiante == null) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            EstudianteFormulario(
                onGuardar = { nuevoEstudiante ->
                    estudianteViewModel.guardarEstudiante(nuevoEstudiante)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    } else {
        DelfinControlApp(
            estudianteViewModel = estudianteViewModel,
            asignaturaViewModel = asignaturaViewModel,
            tareaViewModel = tareaViewModel,
            horarioViewModel = horarioViewModel,
            docenteViewModel = docenteViewModel,
            actividadAfiViewModel = actividadAfiViewModel
        )
    }
}

@Composable
fun DelfinControlApp(
    estudianteViewModel: EstudianteViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    tareaViewModel: TareaViewModel,
    horarioViewModel: HorarioViewModel,
    docenteViewModel: DocenteViewModel,
    actividadAfiViewModel: ActividadAfiViewModel
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HORARIO) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destino ->
                item(
                    icon = {
                        Icon(
                            imageVector = destino.icon,
                            contentDescription = destino.label
                        )
                    },
                    label = { Text(destino.label) },
                    selected = destino == currentDestination,
                    onClick = { currentDestination = destino }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.HORARIO -> {
                    HorariosScreen(
                        horarioViewModel = horarioViewModel,
                        asignaturaViewModel = asignaturaViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.TAREAS -> {
                    TareasScreen(
                        tareaViewModel = tareaViewModel,
                        asignaturaViewModel = asignaturaViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.MATERIAS -> {
                    AsignaturasScreen(
                        asignaturaViewModel = asignaturaViewModel,
                        docenteViewModel = docenteViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.AFIS -> {
                    AfisScreen(
                        viewModel = actividadAfiViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.PROFILE -> {
                    ProfileScreen(
                        estudianteViewModel = estudianteViewModel,
                        asignaturaViewModel = asignaturaViewModel,
                        actividadAfiViewModel = actividadAfiViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    HORARIO("Horario", Icons.Default.Home),
    TAREAS("Tareas", Icons.Default.CheckCircle),
    MATERIAS("Materias", Icons.Default.Star),
    AFIS("AFIs", Icons.Default.Info),
    PROFILE("Perfil", Icons.Default.Person),
}