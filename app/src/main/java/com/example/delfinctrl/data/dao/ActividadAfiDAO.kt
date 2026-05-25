package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.ActividadAFI
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadAfiDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarAsistencia(actividadAFI: ActividadAFI)

    @Update
    suspend fun actualizarActividad(actividadAFI: ActividadAFI)

    @Delete
    suspend fun eliminarActividad(actividadAFI: ActividadAFI)

    @Query ("SELECT IFNULL(SUM(horas), 0) FROM actividades_afi")
    fun calcularHorasAFI(): Flow<Int>

    @Query ("SELECT * FROM actividades_afi ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<ActividadAFI>>
}