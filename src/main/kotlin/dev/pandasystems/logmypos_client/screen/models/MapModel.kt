package dev.pandasystems.logmypos_client.screen.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

class MapModel : ScreenModel {
    val mapState by mutableStateOf(MapState())
    val mapViewportState by mutableStateOf(MapViewportState().apply {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    })
}