package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import com.example.delfinctrl.data.model.AsignaturaHorario
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignaturaHorarioDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarAsignatura(asignaturaHorario: AsignaturaHorario)

    @Update
    suspend fun actualizarAsignatura(asignaturaHorario: AsignaturaHorario)

    @Delete
    suspend fun eliminarAsignatura(asignaturaHorario: AsignaturaHorario)

    @Query("SELECT * FROM asignatura_horario ORDER BY inicio, fin DESC")
    fun obtenerTodas(): Flow<List<AsignaturaHorario>>

    @Query("SELECT * FROM asignatura_horario WHERE horarioId = :id")
    fun obtenerPorHorario(id: Int): Flow<List<AsignaturaHorario>>

    @Query("SELECT * FROM asignatura_horario WHERE dia = :dia")
    fun obtenerAsiganturasPorDia(dia: Int): Flow<List<AsignaturaHorario>>
}