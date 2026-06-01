package com.dudhs.budgetorganizer.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.dudhs.budgetorganizer.R // Imports your new app logo

class NotificationHelper(private val context: Context) {
    private val channelId = "budget_warnings"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Budget Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts you when you reach budget milestones."
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showProgressWarning(spent: Float, target: Float, percent: Int) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Budget Alert: $percent% Reached")
            .setContentText("You have used ${formatMoney(spent)} of your ${formatMoney(target)} limit.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(percent, builder.build())
    }

    fun showTestNotification() {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Test Successful")
            .setContentText("SMS parsed and notifications are working!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(9999, builder.build())
    }
}