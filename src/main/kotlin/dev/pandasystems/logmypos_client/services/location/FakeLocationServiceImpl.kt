package dev.pandasystems.logmypos_client.services.location

import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.models.location.LocationSearch

class FakeLocationServiceImpl : LocationService {
	override var selectedLocation: LocationData? = null

	override suspend fun findLocations(latitude: Double, longitude: Double): List<LocationSearch> {
		return emptyList()
	}

	override suspend fun queryLocations(text: String): List<LocationSearch> {
		return emptyList()
	}

	override suspend fun selectLocation(latitude: Double, longitude: Double) {

	}

	override fun clearSelection() {
		selectedLocation = null
	}
}