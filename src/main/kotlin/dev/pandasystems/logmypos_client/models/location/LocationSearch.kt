package dev.pandasystems.logmypos_client.models.location

import com.mapbox.geojson.Point
import dev.pandasystems.logmypos_client.models.Address

interface LocationSearch {
	val name: String
	val formattedAddress: String?
	val distanceMeters: Double?
	
	suspend fun resolve(): LocationData
}

data class FakeLocationSearch(
	override val name: String,
	override val formattedAddress: String?,
	override val distanceMeters: Double?
) : LocationSearch {
	override suspend fun resolve(): LocationData {
		return FakeLocationData(name, Address(formattedAddress), Point.fromLngLat(0.0, 0.0), null)
	}
}