package com.example.delfinctrl.data.repository

import com.example.delfinctrl.data.dao.DocenteDAO
import com.example.delfinctrl.data.model.Docente
import kotlinx.coroutines.flow.Flow

class DocenteRepository(
    private val docenteDAO: DocenteDAO
) {
    fun obtenerTodos(): Flow<List<Docente>> = docenteDAO.obtenerTodos()

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
