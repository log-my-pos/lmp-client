package dev.pandasystems.logmypos_client.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "deleted_entries")
data class DeletedEntry(
    @PrimaryKey
    val cloudId: Uuid
)
