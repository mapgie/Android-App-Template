package com.example.myapp.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

// Every alarm intent is stamped with a unique data URI. PendingIntent identity is
// (request code + Intent.filterEquals), and filterEquals ignores extras — with only
// hashCode-derived request codes, two ids whose hashes collide would silently cancel
// or clobber each other's alarms. The URI makes every id a distinct intent regardless
// of request code. If you change this identity scheme in a shipped app, sweep-cancel
// the old form once on MY_PACKAGE_REPLACED or old alarms stay armed alongside new ones.
@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules a reminder to fire at [fireAt].
     *
     * Does nothing if [fireAt] is in the past. Uses an exact alarm when permitted;
     * without the exact-alarm permission (Android 12+) it falls back to an inexact
     * alarm, so the reminder is delayed by system batching rather than dropped.
     * Surface the permission state in your settings UI (see PermissionHelper) so
     * users can restore exact delivery.
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

        val pending = buildPendingIntent(id, title, deliveryMode)
        if (canScheduleExactAlarms()) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fireAt.toEpochMilli(),
                pending
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fireAt.toEpochMilli(),
                pending
            )
        }
    }

    /**
     * Cancels any pending alarm for [id]. Safe to call when no alarm is scheduled.
     */
    fun cancel(id: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
            .setPackage(context.packageName)
            .setData(alarmUri(id))
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode(id),
            intent,
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

    private fun alarmUri(id: String): Uri = Uri.parse("myapp://alarm/$id")

    private fun buildPendingIntent(id: String, title: String, deliveryMode: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            setPackage(context.packageName)
            data = alarmUri(id)
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
