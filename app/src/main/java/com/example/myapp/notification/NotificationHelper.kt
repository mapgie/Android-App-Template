package com.example.myapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.myapp.MainActivity
import com.example.myapp.R

object NotificationHelper {
    // Three-channel split for different delivery modes.
    // Channel settings (importance, sound, vibration) are immutable once created on a device,
    // so new channel IDs are required whenever those settings need to change on existing installs.
    const val CHANNEL_APP_REMINDERS_ALARM  = "app_reminders_alarm_v1"
    const val CHANNEL_APP_REMINDERS_NOTIF  = "app_reminders_notif_v1"
    const val CHANNEL_APP_REMINDERS_SILENT = "app_reminders_silent_v1"

    const val EXTRA_NOTIFICATION_ID = "notification_id"

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Alarm channel: uses alarm sound, vibration, and DND bypass if access is granted.
        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alarmAudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_APP_REMINDERS_ALARM,
                context.getString(R.string.channel_app_reminders_alarm_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_app_reminders_alarm_desc)
                setSound(alarmSoundUri, alarmAudioAttributes)
                enableVibration(true)
                // Only takes effect if the user has granted Do Not Disturb access.
                // Wire up ACCESS_NOTIFICATION_POLICY permission and a settings deep-link
                // if your app needs DND bypass (see AndroidManifest.xml comments).
                if (nm.isNotificationPolicyAccessGranted) {
                    setBypassDnd(true)
                }
            }
        )

        // Notification channel: standard notification sound, vibration, no DND bypass.
        val notifSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notifAudioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_APP_REMINDERS_NOTIF,
                context.getString(R.string.channel_app_reminders_notif_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_app_reminders_notif_desc)
                setSound(notifSoundUri, notifAudioAttributes)
                enableVibration(true)
            }
        )

        // Silent channel: low importance, no sound, no vibration.
        nm.createNotificationChannel(
            NotificationChannel(
                CHANNEL_APP_REMINDERS_SILENT,
                context.getString(R.string.channel_app_reminders_silent_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_app_reminders_silent_desc)
                setSound(null, null)
                enableVibration(false)
            }
        )
    }

    /**
     * Shows a generic reminder notification. Tapping opens the app's main activity.
     *
     * @param id     Stable identifier for this notification (used for cancel/update).
     * @param title  Notification title.
     * @param body   Notification body text.
     * @param channelId  One of the CHANNEL_APP_REMINDERS_* constants. Defaults to the
     *                   standard notification channel.
     */
    fun showReminder(
        context: Context,
        id: String,
        title: String,
        body: String,
        channelId: String = CHANNEL_APP_REMINDERS_NOTIF,
    ) {
        val notifyId = id.hashCode()
        val openIntent = PendingIntent.getActivity(
            context,
            notifyId,
            Intent(context, MainActivity::class.java).apply {
                setPackage(context.packageName)
                putExtra(EXTRA_NOTIFICATION_ID, id)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(notifyId, notification)
        }
    }

    // TODO: Add app-specific notification methods here.
}
