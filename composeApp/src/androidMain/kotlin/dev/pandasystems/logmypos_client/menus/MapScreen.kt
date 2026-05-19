package dev.pandasystems.logmypos_client.menus

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import dev.pandasystems.logmypos_client.menus.mapscreen.MapLocationEntryOverlay
import dev.pandasystems.logmypos_client.menus.mapscreen.MapLocationEntryOverlayRoute
import dev.pandasystems.logmypos_client.menus.mapscreen.MapMainOverlay
import dev.pandasystems.logmypos_client.menus.mapscreen.MapMainOverlayRoute
import kotlinx.serialization.Serializable

@Serializable
data object MapRoute

@Preview
@Composable
fun MapScreen(
	navController: NavHostController? = null,
) {
	val mapNavController = rememberNavController()

	Box(modifier = Modifier.fillMaxSize()) {
		Map()

		Box(
			modifier = Modifier
				.align(Alignment.TopCenter)
				.fillMaxSize()
		) {
			NavHost(mapNavController, startDestination = MapMainOverlayRoute) {
				composable<MapMainOverlayRoute> { MapMainOverlay(navController, mapNavController) }
				composable<MapLocationEntryOverlayRoute> { MapLocationEntryOverlay(mapNavController) }
			}
		}
	}
}

@Composable
private fun Map() {
	MapboxMap(
		modifier = Modifier.fillMaxSize(),
		style = { MapStyle(style = "mapbox://styles/julianmaggio/cmoijn6tp002201sfdm0nab23") },
		mapViewportState = rememberMapViewportState {
			setCameraOptions {
				zoom(2.0)
				center(Point.fromLngLat(-98.0, 39.5))
				pitch(0.0)
				bearing(0.0)
			}
		},
		scaleBar = {},
		logo = {},
		attribution = {},
		compass = {
			Compass(
				modifier = Modifier
					.systemBarsPadding(),
				contentPadding = PaddingValues(16.dp, end = 0.dp, top = 0.dp, bottom = 16.dp),
				alignment = Alignment.BottomStart,
			)
		},
	)
}