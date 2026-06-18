package dev.pandasystems.logmypos_client.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.utils.Logger
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params), KoinComponent {
    private val repository: JournalRepository by inject()
    private val apiService: LocationApiService by inject()
    private val authService: AuthService by inject()

    private val logger = Logger("SyncWorker")

    override suspend fun doWork(): Result {
        // Only sync if user is logged in
        if (!authService.isLoggedIn.value) {
            return Result.success()
        }

        return try {
            logger.debug("Starting sync")

            // 0. Sync deletions (Deletion sync)
            val deletedEntries = repository.getDeletedEntries()
            logger.debug("Found ${deletedEntries.size} deleted entries")
            for (deleted in deletedEntries) {
                logger.debug("Processing deleted entry with cloudId ${deleted.cloudId}")
                val success = apiService.deleteLocation(deleted.cloudId)
                if (success) {
                    repository.removeDeletedEntry(deleted.cloudId)
                    logger.debug("Deleted entry with cloudId ${deleted.cloudId}")
                } else {
                    logger.error("Failed to delete entry with cloudId ${deleted.cloudId}")
                }
            }

            // 1. Fetch from cloud (Downward sync)
            val cloudLocations = apiService.getLocations()
            val currentlyDeleted = repository.getDeletedEntries().map { it.cloudId }.toSet()
            logger.debug("Found ${cloudLocations.size} cloud locations")
            for (cloudLoc in cloudLocations) {
                logger.debug("Processing cloud location with id ${cloudLoc.id}")
                if (currentlyDeleted.contains(cloudLoc.id)) {
                    logger.debug("Skipping cloud location with id ${cloudLoc.id} because it is currently deleted")
                    continue
                }

                val localEntry = repository.getEntryByCloudId(cloudLoc.id)
                if (localEntry == null) {
                    logger.debug("Cloud location with id ${cloudLoc.id} not found in local database")
                    val createdAt = try {
                        cloudLoc.created_at?.let { LocalDateTime.parse(it.substring(0, 19)) }
                    } catch (e: Exception) {
                        null
                    } ?: Instant.fromEpochMilliseconds(System.currentTimeMillis())
                        .toLocalDateTime(TimeZone.currentSystemDefault())

                    val newEntry = JournalEntry(
                        title = cloudLoc.title,
                        description = cloudLoc.description ?: "",
                        latitude = cloudLoc.latitude,
                        longitude = cloudLoc.longitude,
                        date = createdAt,
                        imagePaths = emptyList(),
                        isSynced = true,
                        cloudId = cloudLoc.id
                    )
                    val localId = repository.insert(newEntry)
                    logger.debug("Inserted new entry with id $localId")
                    syncImagesForEntry(localId, cloudLoc.id)
                } else {
                    logger.debug("Cloud location with id ${cloudLoc.id} found in local database")
                    val createdAt = try {
                        cloudLoc.created_at?.let { LocalDateTime.parse(it.substring(0, 19)) }
                    } catch (e: Exception) {
                        null
                    } ?: Instant.fromEpochMilliseconds(System.currentTimeMillis())
                        .toLocalDateTime(TimeZone.currentSystemDefault())

                    val updatedEntry = JournalEntry(
                        id = localEntry.id,
                        title = cloudLoc.title,
                        description = cloudLoc.description ?: "",
                        latitude = cloudLoc.latitude,
                        longitude = cloudLoc.longitude,
                        date = createdAt,
                        imagePaths = localEntry.imagePaths,
                        isSynced = true,
                        cloudId = cloudLoc.id,
                    )
                    repository.update(updatedEntry)
                    logger.debug("Updated entry with cloudId ${cloudLoc.id}")
                    syncImagesForEntry(updatedEntry.id, cloudLoc.id)
                }
            }

            // 2. Sync local changes to cloud (Upward sync)
            val unsynced = repository.getUnsyncedEntries()
            logger.debug("Found ${unsynced.size} unsynced entries")
            for (entry in unsynced) {
                logger.debug("Processing unsynced entry with id ${entry.id}")
                val response = if (entry.cloudId == null) {
                    logger.debug("Creating new cloud location for entry with id ${entry.id}")
                    apiService.createLocation(
                        title = entry.title,
                        description = entry.description,
                        latitude = entry.latitude,
                        longitude = entry.longitude,
                        creationDate = entry.date
                    )
                } else {
                    logger.debug("Updating cloud location for entry with id ${entry.id}")
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
                    logger.debug("Successfully synced entry with id ${entry.id}")
                    // Upload images
                    uploadLocalImages(cloudId, entry.imagePaths)?.let {
                        repository.update(entry.copy(imagePaths = it))
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private suspend fun syncImagesForEntry(localId: Long, cloudId: Uuid) {
        logger.debug("Syncing images for entry with id $localId")
        val cloudImages = apiService.getImages(cloudId)
        val entry = requireNotNull(repository.getEntryById(localId))

        val currentPaths = entry.imagePaths.toMutableList()
        var changed = false

        for (cloudImage in cloudImages) {
            logger.debug("Processing cloud image with url ${cloudImage.url}")
            if (!currentPaths.contains(cloudImage.url)) {
                currentPaths.add(cloudImage.url)
                logger.debug("Added cloud image to local database")
                changed = true
            }
        }

        if (changed) {
            repository.update(entry.copy(imagePaths = currentPaths))
            logger.debug("Updated images for entry with id $localId")
        }
    }

    private suspend fun uploadLocalImages(cloudId: Uuid, localPaths: List<String>): List<String>? {
        logger.debug("Uploading local images for entry with id $cloudId")
        val imagesToUpload = localPaths.filter { !it.startsWith("http") && File(it).exists() }
        if (imagesToUpload.isNotEmpty()) {
            val response = apiService.uploadImages(cloudId, imagesToUpload)
            if (response != null) {
                return localPaths.toMutableList().apply {
                    removeAll(imagesToUpload)
                    addAll(response.files.map { it.publicUrl })
                }.also {
                    logger.debug("Uploaded images for entry with id $cloudId: ${it.joinToString(", ")}")
                }
            }
        }
        return null
    }
}
