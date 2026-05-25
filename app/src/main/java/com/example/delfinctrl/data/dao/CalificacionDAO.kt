package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.Calificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface CalificacionDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrar(calificacion: Calificacion)

    @Update
    suspend fun actualizar(calificacion: Calificacion)

    @Delete
    suspend fun eliminar(calificacion: Calificacion)

    @Query ("SELECT * FROM calificaciones ORDER BY calificacionId ASC")
    fun obtenerTodas(): Flow<List<Calificacion>>

    @Query("SELECT * FROM calificaciones WHERE asignaturaId = :asignaturaId ORDER BY fecha DESC")
    fun obtenerPorAsignatura(asignaturaId: Int): Flow<List<Calificacion>>
}