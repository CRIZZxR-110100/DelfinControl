package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.data.repository.DocenteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocenteViewModel(private val repository: DocenteRepository) : ViewModel() {

    val docentesState: StateFlow<List<Docente>> = repository.obtenerTodos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarDocente(nombre: String, apellidos: String, email: String, cubiculo: String) {
        viewModelScope.launch {
            val nuevo = Docente(
                docenteId = java.util.UUID.randomUUID().toString(),
                nombre = nombre,
                apellidos = apellidos,
                email = email,
                cubiculo = cubiculo
            )
            repository.registrarDocente(nuevo)
        }
    }

    fun eliminarDocente(docente: Docente) {
        viewModelScope.launch {
            repository.eliminarDocente(docente)
        }
    }
}
