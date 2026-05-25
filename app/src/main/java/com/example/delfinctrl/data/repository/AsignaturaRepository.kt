package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.AsignaturaDAO
import com.example.delfinctrl.data.model.Asignatura
import kotlinx.coroutines.flow.Flow

class AsignaturaRepository(
    private val asignaturaDAO: AsignaturaDAO
) {
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
}
