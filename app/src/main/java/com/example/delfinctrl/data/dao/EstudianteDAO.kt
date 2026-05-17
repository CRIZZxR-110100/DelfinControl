package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.Estudiante
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudianteDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(estudiante: Estudiante)

    @Update
    suspend fun actualizar(estudiante: Estudiante)

    @Delete
    suspend fun eliminar(estudiante: Estudiante)

    @Query ("SELECT * FROM estudiante LIMIT 1")
    fun obtenerEstudiante(): Flow<Estudiante?>
}