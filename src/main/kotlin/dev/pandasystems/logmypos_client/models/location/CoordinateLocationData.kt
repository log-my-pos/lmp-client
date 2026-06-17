package dev.pandasystems.logmypos_client.models.location

import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.Point
import dev.pandasystems.logmypos_client.models.Address

class CoordinateLocationData(override val coordinate: Point) : LocationData {
    override val name: String = "Lat: ${coordinate.latitude().formatted()} Lon: ${coordinate.longitude().formatted()}"
    override val address: Address? = null
    override val boundingBox: BoundingBox? = null

    fun Double.formatted(): String = "%.3f".format(this)
}