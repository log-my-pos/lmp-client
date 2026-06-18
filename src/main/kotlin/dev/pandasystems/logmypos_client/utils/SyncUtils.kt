package dev.pandasystems.logmypos_client.utils

import android.content.Context
import androidx.work.*
import dev.pandasystems.logmypos_client.worker.SyncWorker

object SyncUtils {
    const val SYNC_WORK_NAME = "JournalSync"

    fun triggerSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}
