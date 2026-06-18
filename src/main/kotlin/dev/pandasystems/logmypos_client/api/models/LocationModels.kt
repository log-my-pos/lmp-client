package dev.pandasystems.logmypos_client.api.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CreateLocationRequest(
    val title: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val created_at: String,
)

@Serializable
data class UpdateLocationRequest(
    val id: Uuid,
    val title: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class LocationDto(
    val id: Uuid,
    val title: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val user_id: Uuid,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class LocationResponse(
    val message: String,
    val data: LocationDto
)
