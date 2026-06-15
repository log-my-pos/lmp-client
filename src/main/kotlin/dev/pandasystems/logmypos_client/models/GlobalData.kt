package dev.pandasystems.logmypos_client.models

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapState
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

class GlobalData {
    val mapState by mutableStateOf(MapState())
    val mapViewportState by mutableStateOf(MapViewportState().apply {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    })

    val searchbarState by mutableStateOf(TextFieldState())
}