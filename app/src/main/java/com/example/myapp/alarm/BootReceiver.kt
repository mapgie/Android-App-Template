package com.example.myapp.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapp.notification.NotificationHelper

// Exported so the system can deliver BOOT_COMPLETED and MY_PACKAGE_REPLACED.
// Intent filters are declared in AndroidManifest.xml (uncomment the BootReceiver block).
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) return

        NotificationHelper.createChannels(context)
        WorkManager.getInstance(context)
            .enqueue(OneTimeWorkRequestBuilder<BootWorker>().build())
    }
}
