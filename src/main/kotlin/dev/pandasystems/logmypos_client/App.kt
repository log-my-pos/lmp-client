package dev.pandasystems.logmypos_client

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography

@Composable
fun App(content: @Composable () -> Unit) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(2.0)
            center(Point.fromLngLat(-98.0, 39.5))
            pitch(0.0)
            bearing(0.0)
        }
    }

    CompositionLocalProvider(
        LocalMapViewportStateProvider provides mapViewportState
    ) {
        MaterialTheme(
            typography = hankenGroteskTypography
        ) {
            content()
        }
    }
}