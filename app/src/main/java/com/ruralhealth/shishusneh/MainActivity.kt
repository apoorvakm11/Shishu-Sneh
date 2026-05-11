package com.ruralhealth.shishusneh

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ruralhealth.shishusneh.databinding.ActivityMainBinding
import com.ruralhealth.shishusneh.worker.VaccineReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // 1. Check/Request Notification Permissions (Crucial for Android 13+)
        checkNotificationPermission()
        
        // 2. Setup Daily Background Reminders
        setupWorkManager()
        
        // 3. Trigger a test notification immediately
        triggerImmediateTestNotification()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun setupWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<VaccineReminderWorker>(1, TimeUnit.DAYS)
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "VaccineReminderWork",
            ExistingPeriodicWorkPolicy.REPLACE, // REPLACE to ensure changes in Worker are picked up for testing
            workRequest
        )
    }

    private fun triggerImmediateTestNotification() {
        // We trigger it via WorkManager so we verify the background mechanism is working
        val testWorkRequest = OneTimeWorkRequestBuilder<VaccineReminderWorker>().build()
        WorkManager.getInstance(this).enqueue(testWorkRequest)
        
        // Also fire a direct notification as a fallback to verify system capabilities
        showDirectVerificationNotification()
    }

    private fun showDirectVerificationNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "shishu_sneh_verify"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Verification", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("Shishu Sneh is Active!")
            .setContentText("Hi! Your baby's health tracker is running smoothly in the background. ✨")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(999, builder.build())
            }
        } else {
            notificationManager.notify(999, builder.build())
        }
    }
}
