package com.example.delfinctrl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.delfinctrl.data.database.DelfinDB
import com.example.delfinctrl.data.repository.EstudianteRepository
import com.example.delfinctrl.ui.theme.DelfinControlTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.delfinctrl.ui.EstudianteFormulario
import com.example.delfinctrl.ui.ProfileScreen

import com.example.delfinctrl.ui.viewmodels.EstudianteViewModel

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

@PreviewScreenSizes
@Composable
fun RootScreen() {
    val context = LocalContext.current

    val database = DelfinDB.getDatabase(context)
    val repository = EstudianteRepository(database.estudianteDAO())

    val estudianteViewModel: EstudianteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EstudianteViewModel(repository) as T
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
        DelfinControlApp(estudianteViewModel = estudianteViewModel)
    }
}

@Composable
fun DelfinControlApp(estudianteViewModel: EstudianteViewModel) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destino ->
                item(
                    icon = {
                        Icon(
                            painterResource(destino.icon),
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
                AppDestinations.HOME -> {
                    Greeting(
                        name = "Inicio",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.FAVORITES -> {
                    Greeting(
                        name = "Favoritos",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.PROFILE -> {
                    ProfileScreen(
                        viewModel = estudianteViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    FAVORITES("Favorites", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DelfinControlTheme {
        Greeting("Android")
    }
}