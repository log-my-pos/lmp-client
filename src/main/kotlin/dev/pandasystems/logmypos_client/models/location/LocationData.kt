package dev.pandasystems.logmypos_client.models.location

import com.mapbox.geojson.Point
import dev.pandasystems.logmypos_client.models.Address

interface LocationData {
	val name: String
	val address: Address?
	val coordinate: Point
}