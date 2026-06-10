package dev.pandasystems.logmypos_client.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.User
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import dev.pandasystems.logmypos_client.components.InputField
import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Preview
@Composable
private fun PreviewComposite() {
	MainScreen()
}

@Composable
fun MainScreen() {
	Surface(modifier = Modifier.fillMaxSize()) {
		MapboxMap(
			Modifier.fillMaxSize(),
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
			compass = {}
		)

		Box(
			modifier = Modifier
				.fillMaxSize()
				.systemBarsPadding()
				.padding(16.dp)
		) {
			InputField(
				placeholder = "Enter to search",
				modifier = Modifier.fillMaxWidth(),
				rightContent = {
					IconButton(
						modifier = Modifier
							.padding(8.dp)
							.size(40.dp),
						onClick = {}
					) {
						Icon(
							imageVector = Tabler.Outline.User,
							contentDescription = "User profile",
							modifier = Modifier
								.fillMaxSize()
								.padding(8.dp)
						)
					}
				}
			)
		}
	}
}