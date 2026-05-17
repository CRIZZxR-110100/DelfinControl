package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.delfinctrl.data.model.Asignatura


@Dao
interface AsignaturaDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(asignatura: Asignatura)

    @Update
    suspend fun actualizar(asignatura: Asignatura)

    @Delete
    suspend fun eliminar(asignatura: Asignatura)

    @Query("SELECT * FROM asignaturas ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Asignatura>>
}