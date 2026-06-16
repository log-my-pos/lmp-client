package dev.pandasystems.logmypos_client

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.CrossfadeTransition
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import dev.pandasystems.logmypos_client.models.GlobalData
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.screen.auth.LoginScreen
import dev.pandasystems.logmypos_client.screen.location.LocationDetailScreen
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography
import dev.pandasystems.logmypos_client.utils.SetupPreview
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Preview
@Composable
fun AppPreview() = SetupPreview {
	App()
}

@OptIn(ExperimentalVoyagerApi::class)
@Composable
fun App() {
	val globalData: GlobalData = koinInject()
	val scope = rememberCoroutineScope()

	val repository: JournalRepository = koinInject()
	val entries by repository.allEntries.collectAsState(emptyList())
	val locationService: LocationService = koinInject()

	MaterialTheme(
		typography = hankenGroteskTypography
	) {
		Surface(modifier = Modifier.fillMaxSize()) {
			Navigator(LoginScreen()) { navigator ->
				Box(Modifier.fillMaxSize()) {
					MapboxMap(
						Modifier.fillMaxSize(),
						mapState = globalData.mapState,
						mapViewportState = globalData.mapViewportState,
						onMapClickListener = { point ->
							scope.launch {
								locationService.selectLocation(point.latitude(), point.longitude())
							}
							true
						},
						scaleBar = {},
						logo = {},
						attribution = {},
						compass = {},
						style = { MapStyle(style = "mapbox://styles/julianmaggio/cmoijn6tp002201sfdm0nab23") },
						content = {
							val marker = rememberIconImage(R.drawable.marker)
							val selectedLocation = locationService.selectedLocation
							if (selectedLocation != null) {
								PointAnnotation(selectedLocation.coordinate) {
									iconImage = marker
									iconSize = 0.35
									iconAnchor = IconAnchor.BOTTOM
								}
							}

							entries.forEach { entry ->
								PointAnnotation(Point.fromLngLat(entry.longitude, entry.latitude)) {
									iconImage = marker
									iconSize = 0.35
									iconAnchor = IconAnchor.BOTTOM
									interactionsState.onClicked {
										navigator.push(LocationDetailScreen(entry.id))
										true
									}
								}
							}
						}
					)

					// Composite the current active screen
					CrossfadeTransition(navigator)
				}
			}
		}
	}
}