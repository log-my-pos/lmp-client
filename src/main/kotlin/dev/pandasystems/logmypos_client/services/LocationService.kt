package dev.pandasystems.logmypos_client.services

import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dev.pandasystems.logmypos_client.models.Address
import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.models.location.LocationSearch

class LocationService(
	val placeAutocomplete: PlaceAutocomplete
) {
	var selectedLocation: LocationData? = null
	
	suspend fun findLocations(latitude: Double, longitude: Double): List<LocationSearch> {
		val suggestions = placeAutocomplete.reverse(Point.fromLngLat(longitude, latitude))
		
		var searchEntries: List<LocationSearch> = emptyList()
		
		suggestions.onValue { 
			searchEntries = it.map { entry -> LocationSearchImpl(placeAutocomplete, entry) }
		}
		
		return searchEntries
	}
	
	suspend fun queryLocations(text: String): List<LocationSearch> {
		val suggestions = placeAutocomplete.suggestions(text)

		var searchEntries: List<LocationSearch> = emptyList()

		suggestions.onValue {
			searchEntries = it.map { entry -> LocationSearchImpl(placeAutocomplete, entry) }
		}

		return searchEntries
	}
	
	fun clearSelection() {
		selectedLocation = null
	}
	
	class LocationSearchImpl(
		val placeAutocomplete: PlaceAutocomplete,
		val mapboxData: PlaceAutocompleteSuggestion,
	) : LocationSearch {
		override val name: String
			get() = mapboxData.name
		override val formattedAddress: String?
			get() = mapboxData.formattedAddress
		override val distanceMeters: Double?
			get() = mapboxData.distanceMeters

		override suspend fun resolve(): LocationData {
			var locationData: LocationData? = null
			placeAutocomplete.select(mapboxData)
				.onValue { locationData = LocationDataImpl(it) }
			return locationData ?: throw IllegalStateException("Location data not found")
		}
	}
	
	class LocationDataImpl(
		val mapboxData: PlaceAutocompleteResult
	): LocationData {
		override val name: String
			get() = mapboxData.name
		override val address: Address?
			get() = mapboxData.address?.let {
				Address(
					formattedAddress = it.formattedAddress
				)
			} 
		override val coordinate: Point
			get() = mapboxData.coordinate

	}
}