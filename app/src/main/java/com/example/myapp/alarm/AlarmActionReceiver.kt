package com.example.myapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
class AlarmActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_SNOOZE  = "com.example.myapp.ACTION_SNOOZE"
        const val ACTION_DISMISS = "com.example.myapp.ACTION_DISMISS"
    }

    @Inject lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val nm = NotificationManagerCompat.from(context)

        when (intent.action) {
            ACTION_SNOOZE -> {
                val id = intent.getStringExtra(AlarmReceiver.EXTRA_ID) ?: return
                val title = intent.getStringExtra(AlarmReceiver.EXTRA_TITLE) ?: "Reminder"
                val deliveryMode = intent.getStringExtra(AlarmReceiver.EXTRA_DELIVERY_MODE) ?: "NOTIFICATION"
                nm.cancel(id.hashCode())

                val result = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        alarmScheduler.schedule(
                            id,
                            title,
                            Instant.now().plus(15, ChronoUnit.MINUTES),
                            deliveryMode
                        )
                    } finally {
                        result.finish()
                    }
                }
            }

            ACTION_DISMISS -> {
                val id = intent.getStringExtra(AlarmReceiver.EXTRA_ID) ?: return
                nm.cancel(id.hashCode())
                // TODO: Add ACTION_DONE to mark items complete in your data layer.
            }
        }
    }
}
