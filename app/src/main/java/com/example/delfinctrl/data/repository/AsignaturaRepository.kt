package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.AsignaturaDAO
import com.example.delfinctrl.data.dao.CalificacionDAO
import com.example.delfinctrl.data.dao.DocenteDAO
import com.example.delfinctrl.data.dao.TareaDAO
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.Calificacion
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.data.model.Tarea
import kotlinx.coroutines.flow.Flow

class AsignaturaRepository(
    private val asignaturaDAO: AsignaturaDAO,
    private val tareaDAO: TareaDAO,
    private val calificacionDAO: CalificacionDAO,
    private val docenteDAO: DocenteDAO
) {
    // Este repositorio centraliza la gestión de materias, tareas, notas y profesores
    // --- Asignaturas ---
    fun obtenerTodas(): Flow<List<Asignatura>> = asignaturaDAO.obtenerTodas()

    suspend fun registrarAsignatura(asignatura: Asignatura) {
        asignaturaDAO.registrar(asignatura)
    }

    suspend fun actualizarAsignatura(asignatura: Asignatura) {
        asignaturaDAO.actualizar(asignatura)
    }

    suspend fun eliminarAsignatura(asignatura: Asignatura) {
        asignaturaDAO.eliminar(asignatura)
    }

    fun obtenerCreditosTotales(): Flow<Int> = asignaturaDAO.calcularCreditosTotales()

    // --- Tareas ---
    fun obtenerTareasPorAsignatura(asignaturaId: Int): Flow<List<Tarea>> = tareaDAO.obtenerTareas(asignaturaId)
    
    fun obtenerTodasLasTareas(): Flow<List<Tarea>> = tareaDAO.obtenerTodas()

    suspend fun registrarTarea(tarea: Tarea): Long {
        return tareaDAO.registrarTarea(tarea)
    }

    suspend fun actualizarTarea(tarea: Tarea) {
        tareaDAO.modificarTarea(tarea)
    }

    suspend fun cambiarEstadoTarea(id: Int, estado: Boolean) {
        tareaDAO.cambiarEstado(id, estado)
    }

    suspend fun eliminarTarea(tarea: Tarea) {
        tareaDAO.eliminarTarea(tarea)
    }

    // --- Calificaciones ---
    fun obtenerCalificacionesPorAsignatura(asignaturaId: Int): Flow<List<Calificacion>> = calificacionDAO.obtenerPorAsignatura(asignaturaId)

    suspend fun registrarCalificacion(calificacion: Calificacion) {
        calificacionDAO.registrar(calificacion)
    }

    suspend fun actualizarCalificacion(calificacion: Calificacion) {
        calificacionDAO.actualizar(calificacion)
    }

    suspend fun eliminarCalificacion(calificacion: Calificacion) {
        calificacionDAO.eliminar(calificacion)
    }

    // --- Docentes ---
    fun obtenerTodosLosDocentes(): Flow<List<Docente>> = docenteDAO.obtenerTodos()

    suspend fun registrarDocente(docente: Docente) {
        docenteDAO.insertar(docente)
    }

    suspend fun actualizarDocente(docente: Docente) {
        docenteDAO.actualizar(docente)
    }

    suspend fun eliminarDocente(docente: Docente) {
        docenteDAO.eliminar(docente)
    }
}
