package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity (tableName = "calificaciones",
    foreignKeys = [
        ForeignKey(
            entity = Asignatura::class,
            parentColumns = ["asignaturaId"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        )
    ], indices = [
        Index("asignaturaId")
    ]
)
data class Calificacion(
    @PrimaryKey (autoGenerate = true)
    val calificacionId: Int = 0,

    val asignaturaId: Int,
    val calif: Int,
    val fecha: Long,
    val nota: String
)
