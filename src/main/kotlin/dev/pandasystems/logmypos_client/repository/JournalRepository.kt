package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.DeletedEntry
import dev.pandasystems.logmypos_client.data.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.uuid.Uuid

interface JournalRepository {
    val allEntries: Flow<List<JournalEntry>>

    val unsyncedEntries: Flow<List<JournalEntry>>

    suspend fun getEntryById(id: Long): JournalEntry?

    suspend fun insert(entry: JournalEntry): Long

    suspend fun update(entry: JournalEntry)

    suspend fun delete(entry: JournalEntry)

    suspend fun getUnsyncedEntries(): List<JournalEntry>
    suspend fun getEntryByCloudId(cloudId: Uuid): JournalEntry?

    suspend fun getDeletedEntries(): List<DeletedEntry>
    suspend fun removeDeletedEntry(cloudId: Uuid)
}

class FakeJournalRepositoryImpl : JournalRepository {
    override val allEntries: Flow<List<JournalEntry>> = flowOf(
        listOf(
            JournalEntry(
                id = 0L,
                title = "Fake Entry",
                description = "Really Fake Entry",
                latitude = 0.0,
                longitude = 0.0,
                date = kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                imagePaths = emptyList(),
                isSynced = false
            )
        )
    )

    override val unsyncedEntries: Flow<List<JournalEntry>> = flowOf(emptyList())

    override suspend fun getEntryById(id: Long): JournalEntry {
        return JournalEntry(
            id = id,
            title = "Fake Entry",
            description = "Really Fake Entry",
            latitude = 0.0,
            longitude = 0.0,
            date = kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                .toLocalDateTime(TimeZone.currentSystemDefault()),
            imagePaths = emptyList(),
            isSynced = false
        )
    }

    override suspend fun insert(entry: JournalEntry): Long {
        return 0L
    }

    override suspend fun update(entry: JournalEntry) {}

    override suspend fun delete(entry: JournalEntry) {}

    override suspend fun getUnsyncedEntries(): List<JournalEntry> = emptyList()
    override suspend fun getEntryByCloudId(cloudId: Uuid): JournalEntry? = null
    override suspend fun getDeletedEntries(): List<DeletedEntry> = emptyList()
    override suspend fun removeDeletedEntry(cloudId: Uuid) {}
}
