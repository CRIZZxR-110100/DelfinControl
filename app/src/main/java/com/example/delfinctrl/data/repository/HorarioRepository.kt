package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.HorarioDAO
import com.example.delfinctrl.data.dao.AsignaturaHorarioDAO
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.AsignaturaHorario
import kotlinx.coroutines.flow.Flow

class HorarioRepository(
    private val horarioDAO: HorarioDAO,
    private val asignaturaHorarioDAO: AsignaturaHorarioDAO
) {
    fun obtenerTodosLosHorarios(): Flow<List<Horario>> = horarioDAO.obtenerTodos()

    suspend fun registrarHorario(horario: Horario) {
        horarioDAO.registrarHorario(horario)
    }

    suspend fun actualizarHorario(horario: Horario) {
        horarioDAO.actualizarHorario(horario)
    }

    suspend fun eliminarHorario(horario: Horario) {
        horarioDAO.eliminarHorario(horario)
    }

    // Lógica para Clases dentro de los Horarios (AsignaturaHorario)
    fun obtenerAsignaturasDeHorario(horarioId: Int): Flow<List<AsignaturaHorario>> =
        asignaturaHorarioDAO.obtenerPorHorario(horarioId)

    fun obtenerTodasLasClases(): Flow<List<AsignaturaHorario>> =
        asignaturaHorarioDAO.obtenerTodas()

    suspend fun agregarClaseAHorario(asignaturaHorario: AsignaturaHorario) {
        asignaturaHorarioDAO.agregarAsignatura(asignaturaHorario)
    }

    suspend fun actualizarClaseEnHorario(asignaturaHorario: AsignaturaHorario) {
        asignaturaHorarioDAO.actualizarAsignatura(asignaturaHorario)
    }

    suspend fun eliminarClaseDeHorario(asignaturaHorario: AsignaturaHorario) {
        asignaturaHorarioDAO.eliminarAsignatura(asignaturaHorario)
    }
}
