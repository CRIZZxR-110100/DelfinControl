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
    suspend fun insertar(actividadAFI: ActividadAFI)

    @Update
    suspend fun actualizar(actividadAFI: ActividadAFI)

    @Delete
    suspend fun eliminar(actividadAFI: ActividadAFI)

    @Query ("SELECT * FROM actividades_afi ORDER BY fecha")
    fun obtenerTodas(): Flow<List<ActividadAFI>>
}