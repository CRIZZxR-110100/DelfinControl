package com.example.delfinctrl.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    
    fun scheduleTaskReminders(context: Context, tareaId: Int, titulo: String, fechaEntregaMs: Long) {
        val workManager = WorkManager.getInstance(context)
        
        // Cancelar alarmas anteriores de esta tarea para evitar duplicados
        cancelTaskReminders(context, tareaId)

        val currentTime = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        // Calcular los tiempos para 5, 3 y 1 día antes a las 9:00 AM.
        // Simplificación: solo restamos los días a la fecha de entrega y vemos si está en el futuro.
        val daysToSchedule = listOf(5, 3, 1)

        for (days in daysToSchedule) {
            val triggerTimeMs = fechaEntregaMs - (days * oneDayMs)
            val delayMs = triggerTimeMs - currentTime
            
            // Si el tiempo de activación es mayor a 0, programar.
            // Le damos un pequeño margen de 1 minuto para evitar que se programe si falta menos de un minuto.
            if (delayMs > 60_000) {
                val inputData = Data.Builder()
                    .putString("TITULO_TAREA", titulo)
                    .putInt("DIAS_FALTANTES", days)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .addTag("TAREA_$tareaId")
                    .setInputData(inputData)
                    .build()

                workManager.enqueue(workRequest)
            }
        }
    }

    fun cancelTaskReminders(context: Context, tareaId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("TAREA_$tareaId")
    }
}
