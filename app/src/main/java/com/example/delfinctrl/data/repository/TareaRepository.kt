package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.TareaDAO
import com.example.delfinctrl.data.model.Tarea
import kotlinx.coroutines.flow.Flow

class TareaRepository(
    private val tareaDAO: TareaDAO
) {
    fun obtenerTodasLasTareas(): Flow<List<Tarea>> = tareaDAO.obtenerTodas()

    fun obtenerTareasPorAsignatura(asignaturaId: Int): Flow<List<Tarea>> =
        tareaDAO.obtenerTareas(asignaturaId)

    fun obtenerPendientesPorAsignatura(asignaturaId: Int): Flow<List<Tarea>> =
        tareaDAO.obtenerPendientes(asignaturaId)

    suspend fun registrarTarea(tarea: Tarea) {
        tareaDAO.registrarTarea(tarea)
    }

    suspend fun modificarTarea(tarea: Tarea) {
        tareaDAO.modificarTarea(tarea)
    }

    suspend fun cambiarEstado(id: Int, estado: Boolean) {
        tareaDAO.cambiarEstado(id, estado)
    }

    suspend fun eliminarTarea(tarea: Tarea) {
        tareaDAO.eliminarTarea(tarea)
    }
}
