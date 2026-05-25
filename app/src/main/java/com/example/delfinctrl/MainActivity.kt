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
import androidx.compose.material.icons.filled.AccountBox
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.delfinctrl.data.database.DelfinDB
import com.example.delfinctrl.data.repository.EstudianteRepository
import com.example.delfinctrl.data.repository.AsignaturaRepository
import com.example.delfinctrl.data.repository.HorarioRepository
import com.example.delfinctrl.data.repository.ActividadAfiRepository
import com.example.delfinctrl.ui.theme.DelfinControlTheme
import com.example.delfinctrl.ui.DocentesScreen
import com.example.delfinctrl.ui.EstudianteFormulario
import com.example.delfinctrl.ui.ProfileScreen
import com.example.delfinctrl.ui.AsignaturasScreen
import com.example.delfinctrl.ui.TareasScreen
import com.example.delfinctrl.ui.HorariosScreen
import com.example.delfinctrl.ui.AfisScreen
import com.example.delfinctrl.ui.viewmodels.EstudianteViewModel
import com.example.delfinctrl.ui.viewmodels.AsignaturaViewModel
import com.example.delfinctrl.ui.viewmodels.HorarioViewModel
import com.example.delfinctrl.ui.viewmodels.ActividadAfiViewModel
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.core.content.ContextCompat
import androidx.compose.runtime.LaunchedEffect

class MainActivity : ComponentActivity() {
    private lateinit var database: DelfinDB
    private lateinit var repoEstudiante: EstudianteRepository
    private lateinit var repoAsignatura: AsignaturaRepository
    private lateinit var repoHorario: HorarioRepository
    private lateinit var repoActividadAfi: ActividadAfiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializamos la base de datos y los repositorios necesarios para la app
        database = DelfinDB.getDatabase(applicationContext)
        repoEstudiante = EstudianteRepository(database.estudianteDAO())
        repoAsignatura = AsignaturaRepository(
            asignaturaDAO = database.asignaturaDAO(),
            tareaDAO = database.tareaDAO(),
            calificacionDAO = database.calificacionDAO(),
            docenteDAO = database.docenteDAO()
        )
        repoHorario = HorarioRepository(database.horarioDAO(), database.asignaturaHorarioDAO())
        repoActividadAfi = ActividadAfiRepository(database.actividadAfiDAO())

        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            DelfinControlTheme {
                RootScreen(
                    repoEstudiante,
                    repoAsignatura,
                    repoHorario,
                    repoActividadAfi
                )
            }
        }
    }

    private fun createNotificationChannel() {
        // Configuramos el canal de notificaciones para las tareas (necesario en Android Oreo+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorios de Tareas"
            val descriptionText = "Notificaciones para recordar entregas de tareas"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("TAREAS_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun RootScreen(
    repoEstudiante: EstudianteRepository,
    repoAsignatura: AsignaturaRepository,
    repoHorario: HorarioRepository,
    repoActividadAfi: ActividadAfiRepository
) {
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

    val horarioViewModel: HorarioViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HorarioViewModel(repoHorario) as T
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

    val uiState by estudianteViewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Si el usuario acepta, ya podemos mostrar notificaciones.
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    when (uiState) {
        is com.example.delfinctrl.ui.viewmodels.EstudianteUiState.Loading -> {
            // Se puede mostrar un indicador de carga si se desea, 
            // o simplemente nada para evitar el parpadeo.
        }
        is com.example.delfinctrl.ui.viewmodels.EstudianteUiState.Success -> {
            val estudiante = (uiState as com.example.delfinctrl.ui.viewmodels.EstudianteUiState.Success).estudiante
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
                    horarioViewModel = horarioViewModel,
                    actividadAfiViewModel = actividadAfiViewModel
                )
            }
        }
    }
}

@Composable
fun DelfinControlApp(
    estudianteViewModel: EstudianteViewModel,
    asignaturaViewModel: AsignaturaViewModel,
    horarioViewModel: HorarioViewModel,
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
                        asignaturaViewModel = asignaturaViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.MATERIAS -> {
                    AsignaturasScreen(
                        asignaturaViewModel = asignaturaViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.DOCENTES -> {
                    DocentesScreen(
                        asignaturaViewModel = asignaturaViewModel,
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
    DOCENTES("Profesores", Icons.Default.AccountBox),
    AFIS("AFIs", Icons.Default.Info),
    PROFILE("Perfil", Icons.Default.Person),
}