package com.example.myapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.myapp.data.preferences.AppPreferences
import com.example.myapp.data.preferences.AppPreferencesStore
import com.example.myapp.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

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
