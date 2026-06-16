package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    val allEntries: Flow<List<JournalEntry>>

    suspend fun getEntryById(id: Long): JournalEntry?

    suspend fun insert(entry: JournalEntry): Long

    suspend fun update(entry: JournalEntry)

    suspend fun delete(entry: JournalEntry)

    suspend fun getUnsyncedEntries(): List<JournalEntry>
}
