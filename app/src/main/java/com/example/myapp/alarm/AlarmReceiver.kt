package com.example.myapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.myapp.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ID            = "alarm_id"
        const val EXTRA_TITLE         = "alarm_title"
        const val EXTRA_DELIVERY_MODE = "alarm_delivery_mode"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Reminder"

        val channelId = when (intent.getStringExtra(EXTRA_DELIVERY_MODE) ?: "NOTIFICATION") {
            "ALARM"  -> NotificationHelper.CHANNEL_APP_REMINDERS_ALARM
            "SILENT" -> NotificationHelper.CHANNEL_APP_REMINDERS_SILENT
            else     -> NotificationHelper.CHANNEL_APP_REMINDERS_NOTIF
        }

        NotificationHelper.showReminder(context, id, title, title, channelId)
    }
}
