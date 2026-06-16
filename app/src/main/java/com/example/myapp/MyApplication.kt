package com.example.myapp

import android.app.Application
import com.example.myapp.data.preferences.AppPreferences
import com.example.myapp.data.preferences.AppPreferencesStore
import com.example.myapp.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {

    val preferencesStore by lazy { AppPreferencesStore(this) }

    // Application-level coroutine scope for one-shot background work.
    // Use this for fire-and-forget operations tied to the app lifetime, not individual screens.
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── Add lazily-initialised singletons here ────────────────────────────────
    // Example (uncomment after creating the DB class):
    // val database by lazy { AppDatabase.getInstance(this) }
    // val repository by lazy { MyRepository(database.myDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        // TODO: Uncomment to enable daily background rescheduling of alarms after reboot:
        // WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        //     "boot_reschedule",
        //     ExistingPeriodicWorkPolicy.KEEP,
        //     PeriodicWorkRequestBuilder<BootWorker>(1, TimeUnit.DAYS).build()
        // )
    }
}
