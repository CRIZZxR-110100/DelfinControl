package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: Tarea)

    @Update
    suspend fun actualizar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)

    @Query ("SELECT * FROM tareas ORDER BY fechaDatetime DESC")
    fun obtenerTodas(): Flow<List<Tarea>>
}