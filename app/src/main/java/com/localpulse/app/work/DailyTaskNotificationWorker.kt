package com.localpulse.app.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyTaskNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val channelId = "daily_tasks"
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Tarefas Diárias",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Lembretes das suas tarefas diárias"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = applicationContext.packageManager
            .getLaunchIntentForPackage(applicationContext.packageName)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val messages = listOf(
            "Suas 3 tarefas de hoje estão esperando! 🎯",
            "Complete suas tarefas e ganhe XP hoje! ⭐",
            "Um passo por dia melhora sua reputação! 🚀",
            "Seus clientes merecem o melhor perfil! \uD83D\uDCBC"
        )
        val message = messages[(System.currentTimeMillis() % messages.size).toInt()]

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("LocalPulse")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
        return Result.success()
    }
}
