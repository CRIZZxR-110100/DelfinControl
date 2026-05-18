package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "estudiante")
data class Estudiante(
    @PrimaryKey (autoGenerate = false)
    val estudianteId: String,
    val nombre: String,
    val apellidos: String,
    val institucion: String,
    val facultad: String,
    val programa_educativo: String,
    val afis_requeridas: Int? = null,
    val creditos_requeridos: Int? = null
)
