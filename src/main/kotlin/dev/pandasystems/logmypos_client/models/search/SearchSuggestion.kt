package dev.pandasystems.logmypos_client.models.search

import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion

data class SearchSuggestion(
	val title: String,
	val addressName: String?,
	val distanceMeters: Double?,
	val etaMinutes: Double?,
	val getResult: suspend () -> SearchResult
) {
	companion object {
		val PREVIEW = SearchSuggestion(
			title = "Main St",
			addressName = "123 Main St, London, UK",
			distanceMeters = 0.0,
			etaMinutes = 0.0,
			getResult = {SearchResult.PREVIEW}
		)
	}
}

fun PlaceAutocompleteSuggestion.toSearchSuggestion(getResult: suspend () -> SearchResult): SearchSuggestion {
	return SearchSuggestion(
		title = this.name,
		addressName = this.formattedAddress,
		distanceMeters = 0.0,
		etaMinutes = 0.0,
		getResult = getResult
	)
}