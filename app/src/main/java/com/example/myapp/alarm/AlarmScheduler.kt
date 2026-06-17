package com.example.myapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules a reminder to fire at [fireAt].
     *
     * Does nothing if [fireAt] is in the past or if exact alarms are not permitted
     * (Android 12+). Present the appropriate permission UI before calling this if needed.
     *
     * @param id           Stable identifier used to cancel or reschedule this alarm.
     * @param title        Human-readable label delivered to [AlarmReceiver] for the notification.
     * @param fireAt       When the alarm should fire.
     * @param deliveryMode One of "ALARM", "NOTIFICATION", or "SILENT" — maps to a notification
     *                     channel in [AlarmReceiver]. Defaults to "NOTIFICATION".
     */
    fun schedule(
        id: String,
        title: String,
        fireAt: Instant,
        deliveryMode: String = "NOTIFICATION",
    ) {
        if (fireAt.isBefore(Instant.now())) return
        if (!canScheduleExactAlarms()) return

        val pending = buildPendingIntent(id, title, deliveryMode)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            fireAt.toEpochMilli(),
            pending
        )
    }

    /**
     * Cancels any pending alarm for [id]. Safe to call when no alarm is scheduled.
     */
    fun cancel(id: String) {
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode(id),
            Intent(context, AlarmReceiver::class.java).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pending)
    }

    /**
     * Returns true if the app is allowed to schedule exact alarms.
     * Always true on API < 31.
     */
    fun canScheduleExactAlarms(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) alarmManager.canScheduleExactAlarms()
        else true

    private fun buildPendingIntent(id: String, title: String, deliveryMode: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            setPackage(context.packageName)
            putExtra(AlarmReceiver.EXTRA_ID, id)
            putExtra(AlarmReceiver.EXTRA_TITLE, title)
            putExtra(AlarmReceiver.EXTRA_DELIVERY_MODE, deliveryMode)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode(id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun requestCode(id: String): Int = "reminder_$id".hashCode()
}
