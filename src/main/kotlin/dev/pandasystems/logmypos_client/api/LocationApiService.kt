package dev.pandasystems.logmypos_client.api

import dev.pandasystems.logmypos_client.api.models.CreateLocationRequest
import dev.pandasystems.logmypos_client.api.models.CreateLocationResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LocationApiService(private val api: LogMyPosApi) {
    suspend fun createLocation(
        title: String,
        description: String?,
        latitude: Double,
        longitude: Double
    ): CreateLocationResponse? {
        return try {
            val response = api.client.post("/api/locations/") {
                contentType(ContentType.Application.Json)
                setBody(
                    CreateLocationRequest(
                        title = title,
                        description = description,
                        latitude = latitude,
                        longitude = longitude
                    )
                )
            }
            if (response.status == HttpStatusCode.Created) {
                response.body<CreateLocationResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
