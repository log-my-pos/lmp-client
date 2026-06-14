package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.data.JournalEntryDao
import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalEntryDao: JournalEntryDao) {
    val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()

    suspend fun getEntryById(id: Long): JournalEntry? {
        return journalEntryDao.getEntryById(id)
    }

    suspend fun insert(entry: JournalEntry): Long {
        return journalEntryDao.insertEntry(entry)
    }

    suspend fun update(entry: JournalEntry) {
        journalEntryDao.updateEntry(entry)
    }

    suspend fun delete(entry: JournalEntry) {
        journalEntryDao.deleteEntry(entry)
    }
}
