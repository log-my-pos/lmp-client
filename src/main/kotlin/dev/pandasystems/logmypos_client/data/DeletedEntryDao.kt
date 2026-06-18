package dev.pandasystems.logmypos_client.data

import androidx.room.*
import kotlin.uuid.Uuid

@Dao
interface DeletedEntryDao {
    @Query("SELECT * FROM deleted_entries")
    suspend fun getAllDeletedEntries(): List<DeletedEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedEntry(deletedEntry: DeletedEntry)

    @Delete
    suspend fun deleteDeletedEntry(deletedEntry: DeletedEntry)

    @Query("DELETE FROM deleted_entries WHERE cloudId = :cloudId")
    suspend fun deleteByCloudId(cloudId: Uuid)
}
