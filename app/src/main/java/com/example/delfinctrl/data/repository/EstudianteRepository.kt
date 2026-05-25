package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.EstudianteDAO
import com.example.delfinctrl.data.model.Estudiante
import kotlinx.coroutines.flow.Flow

class EstudianteRepository (
    private val estudianteDAO: EstudianteDAO
) {
    fun obtenerEstudiante(): Flow<Estudiante?> = estudianteDAO.obtenerEstudiante()

    suspend fun registrarEstudiante(estudiante: Estudiante) {
        estudianteDAO.insertar(estudiante)
    }

    suspend fun eliminarEstudiante(estudiante: Estudiante) {
        estudianteDAO.eliminar(estudiante)
    }

    suspend fun actualizarEstudiante(estudiante: Estudiante){
        estudianteDAO.actualizar(estudiante)
    }
}