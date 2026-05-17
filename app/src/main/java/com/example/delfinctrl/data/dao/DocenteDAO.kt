package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.Docente
import kotlinx.coroutines.flow.Flow

@Dao
interface DocenteDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(docente: Docente)

    @Update
    suspend fun actualizar(docente: Docente)

    @Delete
    suspend fun eliminar(docente: Docente)

    @Query ("SELECT * FROM docentes ORDER BY apellidos DESC")
    fun obtenerTodos(): Flow<List<Docente>>
}