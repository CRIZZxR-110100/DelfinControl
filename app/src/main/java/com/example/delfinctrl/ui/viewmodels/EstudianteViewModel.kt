package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Estudiante
import com.example.delfinctrl.data.repository.EstudianteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface EstudianteUiState {
    data object Loading : EstudianteUiState
    data class Success(val estudiante: Estudiante?) : EstudianteUiState
}

class EstudianteViewModel(private val repository: EstudianteRepository) : ViewModel() {

    val uiState: StateFlow<EstudianteUiState> = repository.obtenerEstudiante()
        .map { EstudianteUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EstudianteUiState.Loading
        )

    fun guardarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.registrarEstudiante(estudiante)
        }
    }

    fun eliminarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.eliminarEstudiante(estudiante)
        }
    }

    fun actualizarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.actualizarEstudiante(estudiante)
        }
    }
}