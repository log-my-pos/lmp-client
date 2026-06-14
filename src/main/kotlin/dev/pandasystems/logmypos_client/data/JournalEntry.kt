package dev.pandasystems.logmypos_client.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val date: Long,
    val imagePath: String?,
    val isSynced: Boolean = false
)
