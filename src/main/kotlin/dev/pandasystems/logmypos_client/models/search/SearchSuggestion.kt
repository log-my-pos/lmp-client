package dev.pandasystems.logmypos_client.models.search

import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

data class SearchSuggestion(
	val address: String,
	val formattedAddress: String?,
	val distanceMeters: Double?,
	val etaMinutes: Double?,
) {
	companion object {
		val PREVIEW = SearchSuggestion(
			address = "Main St",
			formattedAddress = "123 Main St, London, UK",
			distanceMeters = 0.0,
			etaMinutes = 0.0,
		)
	}
}

fun PlaceAutocompleteSuggestion.toSearchSuggestion(): SearchSuggestion {
	return SearchSuggestion(
		address = this.name,
		formattedAddress = this.formattedAddress,
		distanceMeters = this.distanceMeters,
		etaMinutes = this.etaMinutes
	)
}