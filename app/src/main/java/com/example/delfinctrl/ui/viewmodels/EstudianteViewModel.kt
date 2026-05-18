package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Estudiante
import com.example.delfinctrl.data.repository.EstudianteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstudianteViewModel(private val repository: EstudianteRepository) : ViewModel() {

    val estudianteState: StateFlow<Estudiante?> = repository.obtenerEstudiante()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun guardarEstudiante(estudiante: Estudiante) {
        viewModelScope.launch {
            repository.registrarEstudiante(estudiante)
        }
    }
}