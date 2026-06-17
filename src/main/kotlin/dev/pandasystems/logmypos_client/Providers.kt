package dev.pandasystems.logmypos_client

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

val LocalSearchBarProvider = staticCompositionLocalOf { TextFieldState() }
val LocalMapViewportStateProvider = compositionLocalOf<MapViewportState> {
    error("LocalMapViewportStateProvider was not provided")
}