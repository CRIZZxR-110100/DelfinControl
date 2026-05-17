package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "horarios")
data class Horario(
    @PrimaryKey (autoGenerate = true)
    val horarioId: Int = 0,
    val nombre: String,
    val fechaInicio: Long,
    val fechaCierre: Long,
)
