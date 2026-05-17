package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "docentes")
data class Docente(
    @PrimaryKey (autoGenerate = false)
    val docenteId: String,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val cubiculo: String
)
