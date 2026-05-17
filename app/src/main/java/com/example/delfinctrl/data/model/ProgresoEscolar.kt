package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "progreso_escolar",
    foreignKeys = [
        ForeignKey(
            entity = Estudiante::class,
            parentColumns = ["estudianteId"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        )
    ], indices = [
        Index("estudianteId")
    ]
)
data class ProgresoEscolar(
    @PrimaryKey (autoGenerate = true)
    val progresoEscolarId: Int = 0,

    val estudianteId: String,
    val creditosTotales: Int = 0,
    val afisTotales: Int = 0
)
