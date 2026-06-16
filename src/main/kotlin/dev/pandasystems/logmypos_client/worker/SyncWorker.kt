package dev.pandasystems.logmypos_client.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.auth.AuthService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val repository: JournalRepository by inject()
    private val apiService: LocationApiService by inject()
    private val authService: AuthService by inject()

    override suspend fun doWork(): Result {
        // Only sync if user is logged in
        if (!authService.isLoggedIn.value) {
            return Result.success()
        }

        val unsynced = repository.getUnsyncedEntries()
        if (unsynced.isEmpty()) return Result.success()

        var allSuccess = true
        for (entry in unsynced) {
            try {
                val response = apiService.createLocation(
                    title = entry.title,
                    description = entry.description,
                    latitude = entry.latitude,
                    longitude = entry.longitude
                )
                if (response != null) {
                    repository.update(entry.copy(isSynced = true))
                } else {
                    allSuccess = false
                }
            } catch (e: Exception) {
                allSuccess = false
            }
        }

        return if (allSuccess) Result.success() else Result.retry()
    }
}
