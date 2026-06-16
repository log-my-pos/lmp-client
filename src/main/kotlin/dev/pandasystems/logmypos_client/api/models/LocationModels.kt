package dev.pandasystems.logmypos_client.api.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateLocationRequest(
    val title: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class LocationDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val user_id: String,
    val created_at: String? = null,
    val updated_at: String? = null
)

@Serializable
data class CreateLocationResponse(
    val message: String,
    val data: LocationDto
)
