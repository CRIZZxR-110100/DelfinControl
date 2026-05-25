package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

import androidx.room.PrimaryKey

@Entity(tableName = "asignatura_horario",
    foreignKeys = [
        ForeignKey(
            entity = Horario::class,
            parentColumns = ["horarioId"],
            childColumns = ["horarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey (
            entity = Asignatura::class,
            parentColumns = ["asignaturaId"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("horarioId"),
        Index("asignaturaId")
    ]
)
data class AsignaturaHorario(
    @PrimaryKey(autoGenerate = true) val claseId: Int = 0,
    val horarioId: Int,
    val asignaturaId: Int,
    val dia: Int = 0,
    val inicio: Int,
    val fin: Int,
    val salon: String? = null
)
