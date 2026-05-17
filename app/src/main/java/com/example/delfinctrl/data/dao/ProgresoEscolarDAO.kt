package com.example.delfinctrl.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.delfinctrl.data.model.ProgresoEscolar
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgresoEscolarDAO {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(progresoEscolar: ProgresoEscolar)

    @Update
    suspend fun actualizar(progresoEscolar: ProgresoEscolar)

    @Delete
    suspend fun eliminar(progresoEscolar: ProgresoEscolar)

    @Query("SELECT * FROM progreso_escolar")
    fun obtenerProgreso(): Flow<List<ProgresoEscolar>>
}