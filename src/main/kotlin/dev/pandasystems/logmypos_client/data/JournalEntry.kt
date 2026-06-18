package dev.pandasystems.logmypos_client.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

@Entity(
    tableName = "journal_entries",
    indices = [Index(value = ["cloudId"], unique = true)]
)
data class JournalEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val date: LocalDateTime,
    val imagePaths: List<String>,
    val isSynced: Boolean = false,
    val cloudId: Uuid? = null
)
