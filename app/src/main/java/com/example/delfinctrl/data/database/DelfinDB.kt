package com.example.delfinctrl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.delfinctrl.data.dao.ActividadAfiDAO
import com.example.delfinctrl.data.dao.AsignaturaDAO
import com.example.delfinctrl.data.dao.AsignaturaHorarioDAO
import com.example.delfinctrl.data.dao.CalificacionDAO
import com.example.delfinctrl.data.dao.DocenteDAO
import com.example.delfinctrl.data.dao.EstudianteDAO
import com.example.delfinctrl.data.dao.HorarioDAO
import com.example.delfinctrl.data.dao.ProgresoEscolarDAO
import com.example.delfinctrl.data.dao.TareaDAO
import com.example.delfinctrl.data.model.ActividadAFI
import com.example.delfinctrl.data.model.Asignatura
import com.example.delfinctrl.data.model.AsignaturaHorario
import com.example.delfinctrl.data.model.Calificacion
import com.example.delfinctrl.data.model.Docente
import com.example.delfinctrl.data.model.Estudiante
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.ProgresoEscolar
import com.example.delfinctrl.data.model.Tarea

@Database(
    entities = [
        ActividadAFI::class,
        Estudiante::class,
        ProgresoEscolar::class,
        Docente::class,
        Horario::class,
        Asignatura::class,
        Tarea::class,
        Calificacion::class,
        AsignaturaHorario::class,
    ],
    version = 5,
    exportSchema = false
)
abstract class DelfinDB : RoomDatabase() {
    abstract fun actividadAfiDAO(): ActividadAfiDAO
    abstract fun estudianteDAO(): EstudianteDAO
    abstract fun progresoEscolarDAO(): ProgresoEscolarDAO
    abstract fun docenteDAO(): DocenteDAO
    abstract fun horarioDAO(): HorarioDAO
    abstract fun asignaturaDAO(): AsignaturaDAO
    abstract fun tareaDAO(): TareaDAO
    abstract fun calificacionDAO(): CalificacionDAO
    abstract fun asignaturaHorarioDAO(): AsignaturaHorarioDAO

    companion object {
        @Volatile
        private var INSTANCE: DelfinDB? = null

        // Singleton para evitar múltiples instancias de la base de datos abiertas al mismo tiempo
        fun getDatabase(context: Context): DelfinDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DelfinDB::class.java,
                    "delfin_db"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { INSTANCE = it }
            }
        }
    }
}