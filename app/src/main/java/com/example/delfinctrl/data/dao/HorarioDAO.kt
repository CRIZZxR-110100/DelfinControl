package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.delfinctrl.data.model.Horario

@Dao
interface HorarioDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(horario: Horario)

    @Update
    suspend fun actualizar(horario: Horario)

    @Delete
    suspend fun eliminar(horario: Horario)

    @Query("SELECT * FROM horarios ORDER BY fechaInicio DESC")
    fun obtenerTodos(): Flow<List<Horario>>
}