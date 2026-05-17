package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "actividades_afi")
data class ActividadAFI(
    @PrimaryKey (autoGenerate = true)
    val actividadId: Int = 0,

    val nombre: String,
    val fecha: Long,
    val lugar: String? = null,
    val horas: Int
)