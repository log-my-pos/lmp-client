package dev.pandasystems.logmypos_client.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntry?

    @Query("SELECT * FROM journal_entries WHERE isSynced = 0")
    suspend fun getUnsyncedEntries(): List<JournalEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)
}
