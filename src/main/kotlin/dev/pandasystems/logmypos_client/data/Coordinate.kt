package dev.pandasystems.logmypos_client.data

import com.mapbox.geojson.Point

class Coordinate(
    val latitude: Double,
    val longitude: Double
) {
    val asPair get() = Pair(latitude, longitude)
    val asMapBoxPoint get(): Point = Point.fromLngLat(latitude, longitude)

    override fun toString(): String = "$latitude, $longitude"
}

val Point.asCoordinate get() = Coordinate(latitude(), longitude())