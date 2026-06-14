package dev.pandasystems.logmypos_client.repository

import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.data.JournalEntryDao
import kotlinx.coroutines.flow.Flow

class JournalRepositoryImpl(private val journalEntryDao: JournalEntryDao) : JournalRepository {
    override val allEntries: Flow<List<JournalEntry>> = journalEntryDao.getAllEntries()

    override suspend fun getEntryById(id: Long): JournalEntry? {
        return journalEntryDao.getEntryById(id)
    }

    override suspend fun insert(entry: JournalEntry): Long {
        return journalEntryDao.insertEntry(entry)
    }

    override suspend fun update(entry: JournalEntry) {
        journalEntryDao.updateEntry(entry)
    }

    override suspend fun delete(entry: JournalEntry) {
        journalEntryDao.deleteEntry(entry)
    }
}
