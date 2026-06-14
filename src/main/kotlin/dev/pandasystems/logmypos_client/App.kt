package dev.pandasystems.logmypos_client

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.screen.location.AddLocationRoute
import dev.pandasystems.logmypos_client.screen.location.AddLocationScreen
import dev.pandasystems.logmypos_client.screen.location.LocationDetailRoute
import dev.pandasystems.logmypos_client.screen.location.LocationDetailScreen
import dev.pandasystems.logmypos_client.screen.main.MainRoute
import dev.pandasystems.logmypos_client.screen.main.MainScreen
import dev.pandasystems.logmypos_client.screen.search.SearchRoute
import dev.pandasystems.logmypos_client.screen.search.SearchScreen
import dev.pandasystems.logmypos_client.services.LocationService
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography
import org.koin.compose.koinInject

@Preview
@Composable
fun App() {
	val navController = rememberNavController()
	val searchTextFieldState = rememberTextFieldState()

	val mapState = rememberMapState()
	val mapViewportState = rememberMapViewportState {
		setCameraOptions {
			zoom(2.0)
			center(Point.fromLngLat(-98.0, 39.5))
			pitch(0.0)
			bearing(0.0)
		}
	}

	val repository: JournalRepository = koinInject()
	val entries by repository.allEntries.collectAsState(emptyList())
	val locationService: LocationService = koinInject()
	
	MaterialTheme(
		typography = hankenGroteskTypography
	) {
		Surface(modifier = Modifier.fillMaxSize()) {
			MapboxMap(
				Modifier.fillMaxSize(),
				mapState = mapState,
				mapViewportState = mapViewportState,
				scaleBar = {},
				logo = {},
				attribution = {},
				compass = {},
				style = { MapboxStandardStyle() },
				content = {
					val selectedLocation = locationService.selectedLocation
					if (selectedLocation != null) {
						PointAnnotation(point = selectedLocation.coordinate)
					}

					entries.forEach { entry ->
						PointAnnotation(
							point = Point.fromLngLat(entry.longitude, entry.latitude),
							pointAnnotationState = remember {
								PointAnnotationState().apply {
									interactionsState.onClicked {
										navController.navigate(
											LocationDetailRoute(
												name = entry.title,
												description = entry.description,
												address = entry.address ?: "",
												imagePath = entry.imagePath
											)
										)
										true
									}
								}
							}
						)
					}
				}
			)

			NavHost(navController = navController, startDestination = MainRoute) {
				composable<MainRoute> {
					MainScreen(
						navController,
						searchTextFieldState,
						mapViewportState
					)
				}
				composable<SearchRoute> {
					SearchScreen(
						navController,
						searchTextFieldState
					)
				}
				composable<LocationDetailRoute> { backStackEntry ->
					val route: LocationDetailRoute = backStackEntry.toRoute()
					LocationDetailScreen(navController, route.name, route.description, route.address, route.imagePath)
				}
				composable<AddLocationRoute> { backStackEntry ->
					val route: AddLocationRoute = backStackEntry.toRoute()
					AddLocationScreen(
						route,
						navController,
						rememberTextFieldState(),
						rememberTextFieldState()
					)
				}
			}
		}
	}
}