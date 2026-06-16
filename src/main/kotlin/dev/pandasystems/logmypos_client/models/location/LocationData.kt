package dev.pandasystems.logmypos_client.models.location

import com.mapbox.geojson.Point
import dev.pandasystems.logmypos_client.models.Address

interface LocationData {
	val name: String
	val address: Address?
	val coordinate: Point
}

data class FakeLocationData(
	override val name: String,
	override val address: Address?,
	override val coordinate: Point
) : LocationData