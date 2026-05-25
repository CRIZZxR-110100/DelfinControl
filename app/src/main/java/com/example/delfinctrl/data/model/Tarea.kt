package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "tareas",
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
data class Tarea(
    @PrimaryKey (autoGenerate = true)
    val tareaId: Int = 0,

    val asignaturaId: Int,
    val titulo: String,
    val fechaDatetime: Long,
    val nota: String,
    val estado: Boolean = false
)
