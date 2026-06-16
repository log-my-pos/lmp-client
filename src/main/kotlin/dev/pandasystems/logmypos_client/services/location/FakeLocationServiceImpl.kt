package dev.pandasystems.logmypos_client.services.location

import com.mapbox.geojson.Point
import dev.pandasystems.logmypos_client.models.Address
import dev.pandasystems.logmypos_client.models.location.FakeLocationData
import dev.pandasystems.logmypos_client.models.location.FakeLocationSearch
import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.models.location.LocationSearch

class FakeLocationServiceImpl : LocationService {
	override var selectedLocation: LocationData? = null

	override suspend fun findLocations(latitude: Double, longitude: Double): List<LocationSearch> {
		return listOf(
			FakeLocationSearch("North Colin", "2049 Cremin Parkway, North Colin, NC 30166", 192134.0),
			FakeLocationSearch("South Andra", "824 Luke Fort, South Andra, CO 65684", 243.0),
			FakeLocationSearch("Port Arlenview", "10236 Martin Drives, Port Arlenview, MS 89256", 23.0),
			FakeLocationSearch("Lake Rosette", "Apt. 124 5648 Sanford Villages, Lake Rosette, NV 88035", 43354.0),
		)
	}

	override suspend fun queryLocations(text: String): List<LocationSearch> {
		return listOf(
			FakeLocationSearch("North Colin", "2049 Cremin Parkway, North Colin, NC 30166", 192134.0),
			FakeLocationSearch("South Andra", "824 Luke Fort, South Andra, CO 65684", 243.0),
			FakeLocationSearch("Port Arlenview", "10236 Martin Drives, Port Arlenview, MS 89256", 23.0),
			FakeLocationSearch("Lake Rosette", "Apt. 124 5648 Sanford Villages, Lake Rosette, NV 88035", 43354.0),
		)
	}

	override suspend fun selectLocation(latitude: Double, longitude: Double) {
		selectedLocation = FakeLocationData("North Colin", Address("2049 Cremin Parkway, North Colin, NC 30166"), Point.fromLngLat(longitude, latitude))
	}

	override fun clearSelection() {
		selectedLocation = null
	}
}