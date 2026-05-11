package com.ruralhealth.shishusneh.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ruralhealth.shishusneh.MainActivity
import com.ruralhealth.shishusneh.R
import com.ruralhealth.shishusneh.model.AppDatabase
import java.util.Calendar

class VaccineReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("VaccineReminderWorker", "Background check started...")
        
        val dao = AppDatabase.getDatabase(context).shishuDao()
        val pendingVaccines = dao.getPendingVaccinesSync()

        val now = Calendar.getInstance().timeInMillis
        val daysInMs = 24 * 60 * 60 * 1000L

        // Look for vaccines due in the next 3 days
        val upcoming = pendingVaccines.filter { 
            it.dueDateMillis >= (now - (30 * daysInMs)) && (it.dueDateMillis - now) <= 3 * daysInMs
        }

        if (upcoming.isNotEmpty()) {
            val vaccineNames = upcoming.joinToString { it.name }
            showNotification(
                1001,
                "Vaccination Reminder",
                "Hi Ma, $vaccineNames is due for your baby. Keeping up with vaccinations ensures a healthy future! ❤️"
            )
        } else {
            // TEST NOTIFICATION: Ensuring visibility for the user
            showNotification(
                1002,
                "Shishu Sneh is Active!",
                "Hi Ma! Your baby's health tracker is running smoothly in the background. We'll alert you here for any upcoming vaccines. ✨"
            )
        }

        return Result.success()
    }

    private fun showNotification(id: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "shishu_sneh_vax_reminders"

        // Create Channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Baby Health Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent reminders for baby vaccinations"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open app when clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_heart) // Using your heart icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH) // For heads-up notification
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Final permission check before notifying
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(id, builder.build())
                Log.d("VaccineReminderWorker", "Notification sent successfully (ID: $id)")
            } else {
                Log.e("VaccineReminderWorker", "Cannot show notification: Permission Denied")
            }
        } else {
            notificationManager.notify(id, builder.build())
            Log.d("VaccineReminderWorker", "Notification sent successfully (ID: $id)")
        }
    }
}
