package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.data.model.Tarea
import com.example.delfinctrl.data.repository.AsignaturaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AsignaturaViewModel(private val repository: AsignaturaRepository) : ViewModel() {

    // --- Asignaturas ---
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

    fun actualizarAsignatura(asignatura: Asignatura) {
        viewModelScope.launch {
            repository.actualizarAsignatura(asignatura)
        }
    }

    fun eliminarAsignatura(asignatura: Asignatura) {
        viewModelScope.launch {
            repository.eliminarAsignatura(asignatura)
        }
    }

    // --- Tareas ---
    val tareasState: StateFlow<List<Tarea>> = repository.obtenerTodasLasTareas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarTarea(titulo: String, fecha: Long, nota: String, asignaturaId: Int, context: android.content.Context) {
        // Registra una nueva tarea y programa sus recordatorios automáticamente
        viewModelScope.launch {
            val nueva = Tarea(
                asignaturaId = asignaturaId,
                titulo = titulo,
                fechaDatetime = fecha,
                nota = nota,
                estado = false
            )
            val id = repository.registrarTarea(nueva).toInt()
            com.example.delfinctrl.worker.NotificationScheduler.scheduleTaskReminders(context, id, titulo, fecha)
        }
    }

    fun actualizarTarea(tarea: Tarea, context: android.content.Context) {
        viewModelScope.launch {
            repository.actualizarTarea(tarea)
            com.example.delfinctrl.worker.NotificationScheduler.scheduleTaskReminders(context, tarea.tareaId, tarea.titulo, tarea.fechaDatetime)
        }
    }

    fun cambiarEstadoTarea(tareaId: Int, completada: Boolean) {
        viewModelScope.launch {
            repository.cambiarEstadoTarea(tareaId, completada)
        }
    }

    fun eliminarTarea(tarea: Tarea, context: android.content.Context) {
        viewModelScope.launch {
            repository.eliminarTarea(tarea)
            com.example.delfinctrl.worker.NotificationScheduler.cancelTaskReminders(context, tarea.tareaId)
        }
    }

    // --- Docentes ---
    val docentesState: StateFlow<List<Docente>> = repository.obtenerTodosLosDocentes()
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

    fun actualizarDocente(docente: Docente) {
        viewModelScope.launch {
            repository.actualizarDocente(docente)
        }
    }

    fun eliminarDocente(docente: Docente) {
        viewModelScope.launch {
            repository.eliminarDocente(docente)
        }
    }

    // --- Calificaciones ---
    fun obtenerCalificaciones(asignaturaId: Int) = repository.obtenerCalificacionesPorAsignatura(asignaturaId)
    
    fun guardarCalificacion(calificacion: com.example.delfinctrl.data.model.Calificacion) {
        viewModelScope.launch {
            repository.registrarCalificacion(calificacion)
        }
    }

    fun actualizarCalificacion(calificacion: com.example.delfinctrl.data.model.Calificacion) {
        viewModelScope.launch {
            repository.actualizarCalificacion(calificacion)
        }
    }

    fun eliminarCalificacion(calificacion: com.example.delfinctrl.data.model.Calificacion) {
        viewModelScope.launch {
            repository.eliminarCalificacion(calificacion)
        }
    }
}
