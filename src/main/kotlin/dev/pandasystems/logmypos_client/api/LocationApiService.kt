package dev.pandasystems.logmypos_client.api

import dev.pandasystems.logmypos_client.api.models.CreateLocationRequest
import dev.pandasystems.logmypos_client.api.models.LocationResponse
import dev.pandasystems.logmypos_client.api.models.UpdateLocationRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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
}
