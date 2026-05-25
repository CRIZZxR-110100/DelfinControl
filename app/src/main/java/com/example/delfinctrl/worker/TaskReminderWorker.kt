package com.example.delfinctrl.worker

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.delfinctrl.R

class TaskReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val tituloTarea = inputData.getString("TITULO_TAREA") ?: "Tienes una tarea pendiente"
        val diasFaltantes = inputData.getInt("DIAS_FALTANTES", 1)
        
        val contentText = if (diasFaltantes == 0) {
            "¡Es hoy! Tu tarea '$tituloTarea' se entrega hoy."
        } else {
            "Faltan $diasFaltantes días para la entrega de tu tarea: '$tituloTarea'."
        }

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(applicationContext, "TAREAS_CHANNEL")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Recordatorio de Tarea")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            // Random ID para no sobrescribir notificaciones si hay varias al mismo tiempo
            val notificationId = System.currentTimeMillis().toInt()
            NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        }

        return Result.success()
    }
}
