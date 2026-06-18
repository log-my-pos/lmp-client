package dev.pandasystems.logmypos_client.api

import dev.pandasystems.logmypos_client.api.models.*
import dev.pandasystems.logmypos_client.utils.Logger
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.io.File
import kotlin.uuid.Uuid

class LocationApiService(private val api: LogMyPosApi) {
    val logger = Logger("LocationApiService")
    
    suspend fun createLocation(
        title: String,
        description: String?,
        latitude: Double,
        longitude: Double,
        creationDate: LocalDateTime
    ): LocationResponse? {
        return try {
            val response = api.client.post("/api/locations/") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateLocationRequest(
                        title = title,
                        description = description,
                        latitude = latitude,
                        longitude = longitude,
                        created_at = creationDate.toInstant(TimeZone.currentSystemDefault()).toString()
                    )
                )
            }
            if (response.status == HttpStatusCode.Created) {
                response.body<LocationResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateLocation(
        id: Uuid,
        title: String,
        description: String?,
        latitude: Double,
        longitude: Double
    ): LocationResponse? {
        return try {
            val response = api.client.patch("/api/locations/$id/") {
                contentType(ContentType.Application.Json)
                setBody(
                    UpdateLocationRequest(
                        id = id,
                        title = title,
                        description = description,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<LocationResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocation(id: Uuid): LocationResponse? {
        return try {
            val response = api.client.get("/api/locations/$id/")
            if (response.status == HttpStatusCode.OK) {
                response.body<LocationResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getLocations(): List<LocationDto> {
        return try {
            val response = api.client.get("/api/locations/")
            if (response.status == HttpStatusCode.OK) {
                response.body<LocationListResponse>().data
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun uploadImages(locationId: Uuid, imagePaths: List<String>): ImageUploadResponse? {
        if (imagePaths.isEmpty()) {
            logger.warning("No images to upload")
            return null
        }

        logger.debug("Uploading images for location $locationId")
        return try {
            val response = api.client.post("/api/locations/image/$locationId") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            imagePaths.forEach { path ->
                                val file = File(path)
                                logger.debug("Uploading image: $path")
                                
                                if (file.exists()) {
                                    logger.debug("Appending image to form data")

                                    val mimeType = when (file.extension.lowercase()) {
                                        "jpg", "jpeg" -> "image/jpeg"
                                        "png" -> "image/png"
                                        "webp" -> "image/webp"
                                        "gif" -> "image/gif"
                                        else -> "application/octet-stream"
                                    }
                                    
                                    append("images", file.readBytes(), Headers.build {
                                        append(HttpHeaders.ContentType, mimeType)
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "form-data; name=\"images\"; filename=\"${file.name}\""
                                        )
                                    })
                                }
                            }
                        }
                    )
                )
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<ImageUploadResponse>().also {
                    logger.debug("Images uploaded successfully")
                }
            } else {
                logger.error("Failed to upload images: ${response.status}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getImages(locationId: Uuid): List<ImageDto> {
        return try {
            val response = api.client.get("/api/locations/image/$locationId")
            if (response.status == HttpStatusCode.OK) {
                response.body<ImageListResponse>().images
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteLocation(id: Uuid): Boolean {
        return try {
            val response = api.client.delete("/api/locations/$id")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
