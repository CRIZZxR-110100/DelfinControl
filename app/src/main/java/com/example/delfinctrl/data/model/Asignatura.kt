package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity (tableName = "asignaturas",
    foreignKeys = [
        ForeignKey(
            entity = Asignatura::class,
            parentColumns = ["asignaturaId"],
            childColumns = ["previa"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("previa")
    ]
)
data class Asignatura(
    @PrimaryKey (autoGenerate = true)
    val asignaturaId: Int = 0,

    val nombre: String,
    val previa: Int? = null,
    val creditos: Int = 0
)
