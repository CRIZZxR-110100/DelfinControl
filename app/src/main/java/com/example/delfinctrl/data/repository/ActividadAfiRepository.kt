package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.ActividadAfiDAO
import com.example.delfinctrl.data.model.ActividadAFI
import kotlinx.coroutines.flow.Flow

class ActividadAfiRepository(
    private val actividadAfiDAO: ActividadAfiDAO
) {
    fun obtenerTodas(): Flow<List<ActividadAFI>> = actividadAfiDAO.obtenerTodas()

    fun obtenerHorasAfiTotales(): Flow<Int> = actividadAfiDAO.calcularHorasAFI()

    suspend fun registrarActividad(actividadAFI: ActividadAFI) {
        actividadAfiDAO.registrarAsistencia(actividadAFI)
    }

    suspend fun actualizarActividad(actividadAFI: ActividadAFI) {
        actividadAfiDAO.actualizarActividad(actividadAFI)
    }

    suspend fun eliminarActividad(actividadAFI: ActividadAFI) {
        actividadAfiDAO.eliminarActividad(actividadAFI)
    }
}
