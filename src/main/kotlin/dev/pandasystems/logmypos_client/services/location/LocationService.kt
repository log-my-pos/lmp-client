package dev.pandasystems.logmypos_client.services.location

import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.models.location.LocationSearch

interface LocationService {
	var selectedLocation: LocationData?
	
	suspend fun findLocations(latitude: Double, longitude: Double): List<LocationSearch>
	suspend fun queryLocations(text: String): List<LocationSearch>
	suspend fun selectLocation(latitude: Double, longitude: Double)
	
	fun clearSelection() {
		selectedLocation = null
	}
}