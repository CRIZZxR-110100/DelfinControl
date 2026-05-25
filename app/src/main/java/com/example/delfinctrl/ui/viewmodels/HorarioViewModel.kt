package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.AsignaturaHorario
import com.example.delfinctrl.data.repository.HorarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class HorarioViewModel(private val repository: HorarioRepository) : ViewModel() {

    val horariosState: StateFlow<List<Horario>> = repository.obtenerTodosLosHorarios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val seleccionadoHorarioId = MutableStateFlow<Int?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val clasesState: StateFlow<List<AsignaturaHorario>> = seleccionadoHorarioId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(emptyList())
            } else {
                repository.obtenerAsignaturasDeHorario(id)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun seleccionarHorario(id: Int?) {
        seleccionadoHorarioId.value = id
    }

    fun guardarHorario(nombre: String, fechaInicio: Long, fechaCierre: Long) {
        viewModelScope.launch {
            val nuevo = Horario(
                nombre = nombre,
                fechaInicio = fechaInicio,
                fechaCierre = fechaCierre
            )
            repository.registrarHorario(nuevo)
        }
    }

    fun eliminarHorario(horario: Horario) {
        viewModelScope.launch {
            if (seleccionadoHorarioId.value == horario.horarioId) {
                seleccionadoHorarioId.value = null
            }
            repository.eliminarHorario(horario)
        }
    }

    fun agregarClase(horarioId: Int, asignaturaId: Int, dia: Int, inicio: Int, fin: Int) {
        viewModelScope.launch {
            val nuevaClase = AsignaturaHorario(
                horarioId = horarioId,
                asignaturaId = asignaturaId,
                dia = dia,
                inicio = inicio,
                fin = fin
            )
            repository.agregarClaseAHorario(nuevaClase)
        }
    }

    fun eliminarClase(asignaturaHorario: AsignaturaHorario) {
        viewModelScope.launch {
            repository.eliminarClaseDeHorario(asignaturaHorario)
        }
    }
}
