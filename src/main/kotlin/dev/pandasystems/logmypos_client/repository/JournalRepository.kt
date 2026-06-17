package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.JournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface JournalRepository {
    val allEntries: Flow<List<JournalEntry>>

    suspend fun getEntryById(id: Long): JournalEntry?

    suspend fun insert(entry: JournalEntry): Long

    suspend fun update(entry: JournalEntry)

    suspend fun delete(entry: JournalEntry)

    suspend fun getUnsyncedEntries(): List<JournalEntry>
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
                address = "",
                date = 0L,
                imagePaths = emptyList(),
                isSynced = false
            )
        )
    )

    override suspend fun getEntryById(id: Long): JournalEntry {
        return JournalEntry(
            id = id,
            title = "Fake Entry",
            description = "Really Fake Entry",
            latitude = 0.0,
            longitude = 0.0,
            address = "",
            date = 0L,
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
}
