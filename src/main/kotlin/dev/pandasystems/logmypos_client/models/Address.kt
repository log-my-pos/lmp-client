package dev.pandasystems.logmypos_client.models

import com.mapbox.search.autocomplete.PlaceAutocompleteAddress

data class Address(
	val formattedAddress: String?
)

fun PlaceAutocompleteAddress.toAddress(): Address {
	return Address(
		formattedAddress = formattedAddress
	)
}