package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Tarea
import com.example.delfinctrl.data.repository.TareaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(private val repository: TareaRepository) : ViewModel() {

    val tareasState: StateFlow<List<Tarea>> = repository.obtenerTodasLasTareas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarTarea(titulo: String, fecha: Long, nota: String, asignaturaId: Int, rutaAdjunto: String = "") {
        viewModelScope.launch {
            val nueva = Tarea(
                asignaturaId = asignaturaId,
                titulo = titulo,
                fechaDatetime = fecha,
                rutaAdjunto = rutaAdjunto,
                nota = nota,
                estado = false
            )
            repository.registrarTarea(nueva)
        }
    }

    fun cambiarEstadoTarea(tareaId: Int, completada: Boolean) {
        viewModelScope.launch {
            repository.cambiarEstado(tareaId, completada)
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repository.eliminarTarea(tarea)
        }
    }
}
