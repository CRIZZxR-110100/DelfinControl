package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.repository.AsignaturaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AsignaturaViewModel(private val repository: AsignaturaRepository) : ViewModel() {

    val asignaturasState: StateFlow<List<Asignatura>> = repository.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val creditosTotalesState: StateFlow<Int> = repository.obtenerCreditosTotales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun guardarAsignatura(nombre: String, creditos: Int, previaId: Int?, docenteId: String? = null) {
        viewModelScope.launch {
            val nueva = Asignatura(
                nombre = nombre,
                creditos = creditos,
                previa = previaId,
                docenteId = docenteId
            )
            repository.registrarAsignatura(nueva)
        }
    }

    fun eliminarAsignatura(asignatura: Asignatura) {
        viewModelScope.launch {
            repository.eliminarAsignatura(asignatura)
        }
    }
}
