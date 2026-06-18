package dev.pandasystems.logmypos_client.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.auth.AuthService
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.uuid.Uuid

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val repository: JournalRepository by inject()
    private val apiService: LocationApiService by inject()
    private val authService: AuthService by inject()

    override suspend fun doWork(): Result {
        // Only sync if user is logged in
        if (!authService.isLoggedIn.value) {
            return Result.success()
        }

        return try {
            // 1. Fetch from cloud (Downward sync)
            val cloudLocations = apiService.getLocations()
            for (cloudLoc in cloudLocations) {
                val localEntry = repository.getEntryByCloudId(cloudLoc.id)
                if (localEntry == null) {
                    val createdAt = try {
                        cloudLoc.created_at?.let { LocalDateTime.parse(it.substring(0, 19)) }
                    } catch (e: Exception) {
                        null
                    } ?: kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                        .toLocalDateTime(TimeZone.currentSystemDefault())

                    val newEntry = JournalEntry(
                        title = cloudLoc.title,
                        description = cloudLoc.description ?: "",
                        latitude = cloudLoc.latitude,
                        longitude = cloudLoc.longitude,
                        address = null,
                        date = createdAt,
                        imagePaths = emptyList(),
                        isSynced = true,
                        cloudId = cloudLoc.id
                    )
                    val localId = repository.insert(newEntry)
                    syncImagesForEntry(localId, cloudLoc.id)
                } else {
                    // Update existing local entry if it was already synced
                    // For now, we prioritize images sync
                    syncImagesForEntry(localEntry.id, cloudLoc.id)
                }
            }

            // 2. Sync local changes to cloud (Upward sync)
            val unsynced = repository.getUnsyncedEntries()
            for (entry in unsynced) {
                val response = if (entry.cloudId == null) {
                    apiService.createLocation(
                        title = entry.title,
                        description = entry.description,
                        latitude = entry.latitude,
                        longitude = entry.longitude,
                        creationDate = entry.date
                    )
                } else {
                    apiService.updateLocation(
                        id = entry.cloudId,
                        title = entry.title,
                        description = entry.description,
                        latitude = entry.latitude,
                        longitude = entry.longitude
                    )
                }

                if (response != null) {
                    val cloudId = response.data.id
                    repository.update(
                        entry.copy(
                            isSynced = true,
                            cloudId = cloudId
                        )
                    )
                    // Upload images
                    uploadLocalImages(cloudId, entry.imagePaths)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun syncImagesForEntry(localId: Long, cloudId: Uuid) {
        val cloudImages = apiService.getImages(cloudId)
        val entry = repository.getEntryById(localId) ?: return

        val currentPaths = entry.imagePaths.toMutableList()
        var changed = false

        for (cloudImage in cloudImages) {
            if (!currentPaths.contains(cloudImage.url)) {
                currentPaths.add(cloudImage.url)
                changed = true
            }
        }

        if (changed) {
            repository.update(entry.copy(imagePaths = currentPaths))
        }
    }

    private suspend fun uploadLocalImages(cloudId: Uuid, localPaths: List<String>) {
        val imagesToUpload = localPaths.filter { !it.startsWith("http") && File(it).exists() }
        if (imagesToUpload.isNotEmpty()) {
            apiService.uploadImages(cloudId, imagesToUpload)
        }
    }
}
