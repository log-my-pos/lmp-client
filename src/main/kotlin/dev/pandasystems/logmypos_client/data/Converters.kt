package dev.pandasystems.logmypos_client.data

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

class Converters {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        return if (data.isEmpty()) emptyList() else data.split(",")
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromUuid(value: String?): Uuid? {
        return value?.let { Uuid.parse(it) }
    }

    @TypeConverter
    fun uuidToString(uuid: Uuid?): String? {
        return uuid?.toString()
    }
}
