package com.example.myapp.alarm

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myapp.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BootWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val alarmScheduler: AlarmScheduler,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.createChannels(applicationContext)

        // TODO: Load your pending reminders here and call AlarmScheduler.schedule() for each.
        // Example:
        //   val pending = myRepository.pendingReminders()
        //   pending.forEach { reminder ->
        //       alarmScheduler.schedule(reminder.id, reminder.title, reminder.fireAt, reminder.deliveryMode)
        //   }

        return Result.success()
    }
}
