# 🐬 DelfinControl — Base Room & Entidades

## Estructura de Paquetes Recomendada

```
com.example.delfinctrl/
├── data/
│   ├── model/
│   │   ├── Materia.kt
│   │   ├── Horario.kt
│   │   └── MateriaEnHorario.kt
│   ├── relations/
│   │   └── HorarioConMaterias.kt       ← Clase de relación para queries
│   ├── dao/
│   │   ├── MateriaDao.kt
│   │   ├── HorarioDao.kt
│   │   └── MateriaEnHorarioDao.kt
│   └── database/
│       └── DelfinDatabase.kt
├── ui/
│   └── theme/
└── MainActivity.kt
```

---

## Dependencias a agregar en `build.gradle.kts` (app)

```kotlin
// Room
val roomVersion = "2.7.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")       // Soporte Coroutines/Flow
ksp("androidx.room:room-compiler:$roomVersion")             // Procesador de anotaciones
```

> También necesitas el plugin KSP en el `build.gradle.kts` raíz y en el de app.

---

## 📦 Entidades

### `Materia.kt`

```kotlin
package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materias")
data class Materia(
    @PrimaryKey(autoGenerate = true)
    val materiaId: Int = 0,

    val nombre: String,                     // "Cálculo Diferencial"
    val codigo: String = "",                // "MAT101" (opcional)
    val profesor: String = "",              // Nombre del docente
    val creditos: Int = 0,                  // Créditos de la materia
    val colorHex: String = "#6650A4"        // Color para UI (Material You)
)
```

---

### `Horario.kt`

```kotlin
package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "horarios")
data class Horario(
    @PrimaryKey(autoGenerate = true)
    val horarioId: Int = 0,

    val nombre: String,             // "Enero - Junio 2026"
    val semestre: Int,              // Número de semestre: 1, 2, 3...
    val anio: Int,                  // 2026
    val activo: Boolean = false     // Solo un horario activo a la vez
)
```

---

### `MateriaEnHorario.kt`

Esta es la tabla **puente** entre `Horario` y `Materia`.  
Permite que la misma materia exista en varios semestres con distintos horarios.

```kotlin
package com.example.delfinctrl.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materias_en_horario",
    foreignKeys = [
        ForeignKey(
            entity = Horario::class,
            parentColumns = ["horarioId"],
            childColumns = ["horarioId"],
            onDelete = ForeignKey.CASCADE   // Al borrar horario, borra sus materias
        ),
        ForeignKey(
            entity = Materia::class,
            parentColumns = ["materiaId"],
            childColumns = ["materiaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("horarioId"),
        Index("materiaId")
    ]
)
data class MateriaEnHorario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val horarioId: Int,             // FK → Horario
    val materiaId: Int,             // FK → Materia

    // Días de clase: bitmask (Lun=1, Mar=2, Mie=4, Jue=8, Vie=16, Sab=32)
    // Ej: Lun+Mie+Vie = 1+4+16 = 21
    val diasClase: Int = 0,

    val horaInicio: String = "",    // "08:00" — formato HH:mm
    val horaFin: String = "",       // "09:30"
    val salon: String = "",         // "Aula 203"

    val calificacion: Float? = null // Calificación final (null = en curso)
)
```

---

## 🔗 Relación: Horario con sus Materias

```kotlin
package com.example.delfinctrl.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.Materia
import com.example.delfinctrl.data.model.MateriaEnHorario

data class HorarioConMaterias(
    @Embedded val horario: Horario,

    @Relation(
        parentColumn = "horarioId",
        entityColumn = "materiaId",
        associateBy = Junction(
            value = MateriaEnHorario::class,
            parentColumn = "horarioId",
            entityColumn = "materiaId"
        )
    )
    val materias: List<Materia>
)
```

---

## 🗂️ DAOs

### `HorarioDao.kt`

```kotlin
package com.example.delfinctrl.data.dao

import androidx.room.*
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.relations.HorarioConMaterias
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(horario: Horario): Long

    @Update
    suspend fun actualizar(horario: Horario)

    @Delete
    suspend fun eliminar(horario: Horario)

    @Query("SELECT * FROM horarios ORDER BY anio DESC, semestre DESC")
    fun obtenerTodos(): Flow<List<Horario>>

    @Query("SELECT * FROM horarios WHERE activo = 1 LIMIT 1")
    fun obtenerActivo(): Flow<Horario?>

    @Transaction
    @Query("SELECT * FROM horarios WHERE horarioId = :id")
    fun obtenerConMaterias(id: Int): Flow<HorarioConMaterias>
}
```

---

### `MateriaDao.kt`

```kotlin
package com.example.delfinctrl.data.dao

import androidx.room.*
import com.example.delfinctrl.data.model.Materia
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(materia: Materia): Long

    @Update
    suspend fun actualizar(materia: Materia)

    @Delete
    suspend fun eliminar(materia: Materia)

    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Materia>>

    @Query("SELECT * FROM materias WHERE materiaId = :id")
    fun obtenerPorId(id: Int): Flow<Materia?>
}
```

---

### `MateriaEnHorarioDao.kt`

```kotlin
package com.example.delfinctrl.data.dao

import androidx.room.*
import com.example.delfinctrl.data.model.MateriaEnHorario
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaEnHorarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(materiaEnHorario: MateriaEnHorario): Long

    @Update
    suspend fun actualizar(materiaEnHorario: MateriaEnHorario)

    @Delete
    suspend fun eliminar(materiaEnHorario: MateriaEnHorario)

    @Query("SELECT * FROM materias_en_horario WHERE horarioId = :horarioId")
    fun obtenerPorHorario(horarioId: Int): Flow<List<MateriaEnHorario>>

    // Registrar calificación al final del semestre
    @Query("UPDATE materias_en_horario SET calificacion = :cal WHERE id = :id")
    suspend fun registrarCalificacion(id: Int, cal: Float)
}
```

---

## 🗄️ Base de Datos

```kotlin
package com.example.delfinctrl.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.delfinctrl.data.dao.HorarioDao
import com.example.delfinctrl.data.dao.MateriaDao
import com.example.delfinctrl.data.dao.MateriaEnHorarioDao
import com.example.delfinctrl.data.model.Horario
import com.example.delfinctrl.data.model.Materia
import com.example.delfinctrl.data.model.MateriaEnHorario

@Database(
    entities = [Materia::class, Horario::class, MateriaEnHorario::class],
    version = 1,
    exportSchema = false
)
abstract class DelfinDatabase : RoomDatabase() {

    abstract fun materiaDao(): MateriaDao
    abstract fun horarioDao(): HorarioDao
    abstract fun materiaEnHorarioDao(): MateriaEnHorarioDao

    companion object {
        @Volatile
        private var INSTANCE: DelfinDatabase? = null

        fun getDatabase(context: Context): DelfinDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DelfinDatabase::class.java,
                    "delfin_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

---

## 📐 Diagrama de Relaciones

```
┌─────────────┐       ┌──────────────────────┐       ┌─────────────┐
│   Horario   │──1:N──│   MateriaEnHorario   │──N:1──│   Materia   │
│─────────────│       │──────────────────────│       │─────────────│
│ horarioId   │       │ id                   │       │ materiaId   │
│ nombre      │       │ horarioId  (FK)      │       │ nombre      │
│ semestre    │       │ materiaId  (FK)      │       │ codigo      │
│ anio        │       │ diasClase            │       │ profesor    │
│ activo      │       │ horaInicio           │       │ creditos    │
└─────────────┘       │ horaFin              │       │ colorHex    │
                      │ salon                │       └─────────────┘
                      │ calificacion         │
                      └──────────────────────┘
```

---

## 💡 Notas de Diseño

| Decisión | Justificación |
|---|---|
| `diasClase` como bitmask `Int` | Permite filtrar días fácilmente con operaciones bit a bit (`diasClase and 1 != 0` = hay lunes) |
| `calificacion: Float?` nullable | `null` indica materia en curso; valor indica semestre terminado |
| `activo` en Horario | Permite mostrar siempre el semestre actual sin búsquedas complejas |
| `Flow<>` en DAOs | Se integra nativamente con `collectAsState()` en Compose |
| Singleton en `DelfinDatabase` | Evita crear múltiples instancias de la BD |

