package dev.pandasystems.logmypos_client.models.search

import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocompleteResult
import dev.pandasystems.logmypos_client.models.Address
import dev.pandasystems.logmypos_client.models.toAddress

data class SearchResult(
	val locationName: String,
	val address: Address?,
	val coordinate: Point,
) {
	companion object {
		val PREVIEW = SearchResult(
			locationName = "123 Main St, London, UK",
			address = null,
			coordinate = Point.fromLngLat(0.0, 0.0),
		)
	}
}

fun PlaceAutocompleteResult.toSearchResult(): SearchResult {
	return SearchResult(
		locationName = this.name,
		address = this.address?.toAddress(),
		coordinate = this.coordinate,
	)
}