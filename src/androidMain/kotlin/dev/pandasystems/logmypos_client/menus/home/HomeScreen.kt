package dev.pandasystems.logmypos_client.menus.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import dev.pandasystems.logmypos_client.navigation.NavHost

object Home

@Preview
@Composable
fun HomeScreen() {
	Box(modifier = Modifier.fillMaxSize()) {
		Map()

		Box(
			modifier = Modifier
				.align(Alignment.TopCenter)
				.fillMaxSize()
		) {
			NavHost(MainHomeMenu) {
				Composer<MainHomeMenu> { MainHomeMenu() }
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