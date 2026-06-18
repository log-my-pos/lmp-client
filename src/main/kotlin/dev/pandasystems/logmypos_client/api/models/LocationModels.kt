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

@Serializable
data class LocationListResponse(
    val message: String,
    val data: List<LocationDto>
)

@Serializable
data class ImageDto(
    val id: String,
    val url: String,
    val path: String,
    val created_at: String? = null
)

@Serializable
data class ImageListResponse(
    val success: Boolean,
    val message: String,
    val images: List<ImageDto>
)

@Serializable
data class ImageUploadResponse(
    val success: Boolean,
    val message: String,
    val files: List<UploadedFileDto>
)

@Serializable
data class UploadedFileDto(
    val originalName: String,
    val storagePath: String,
    val publicUrl: String
)
