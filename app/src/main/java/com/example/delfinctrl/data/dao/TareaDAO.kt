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
    suspend fun registrarTarea(tarea: Tarea): Long

    @Update
    suspend fun modificarTarea(tarea: Tarea)

    @Query ("UPDATE tareas SET estado = :estado WHERE tareaId = :id")
    suspend fun cambiarEstado(id: Int, estado: Boolean)

    @Query ("SELECT * FROM tareas WHERE asignaturaId = :asignatura")
    fun obtenerTareas(asignatura: Int): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE estado = 0 AND asignaturaId = :asignatura")
    fun obtenerPendientes(asignatura: Int): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas ORDER BY fechaDatetime ASC")
    fun obtenerTodas(): Flow<List<Tarea>>

    @Delete
    suspend fun eliminarTarea(tarea: Tarea)
}