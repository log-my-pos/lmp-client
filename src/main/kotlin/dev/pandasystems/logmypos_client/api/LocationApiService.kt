package dev.pandasystems.logmypos_client.api

import dev.pandasystems.logmypos_client.api.models.*
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
        return try {
            val response = api.client.post("/api/locations/image/$locationId") {
                setBody(
                    MultiPartFormDataContent(
                    formData {
                        imagePaths.forEach { path ->
                            val file = File(path)
                            if (file.exists()) {
                                append("images", file.readBytes(), Headers.build {
                                    append(HttpHeaders.ContentType, "image/jpeg")
                                    append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                                })
                            }
                        }
                    }
                ))
            }
            if (response.status == HttpStatusCode.OK) {
                response.body<ImageUploadResponse>()
            } else {
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
}
