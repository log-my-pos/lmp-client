package dev.pandasystems.logmypos_client

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.search.autocomplete.PlaceAutocomplete
import dev.pandasystems.logmypos_client.screen.main.MainRoute
import dev.pandasystems.logmypos_client.screen.main.MainScreen
import dev.pandasystems.logmypos_client.screen.main.location.AddLocationRoute
import dev.pandasystems.logmypos_client.screen.main.location.AddLocationScreen
import dev.pandasystems.logmypos_client.screen.main.location.LocationDetailRoute
import dev.pandasystems.logmypos_client.screen.main.location.LocationDetailScreen
import dev.pandasystems.logmypos_client.models.search.SearchResult
import dev.pandasystems.logmypos_client.screen.main.search.SearchRoute
import dev.pandasystems.logmypos_client.screen.main.search.SearchScreen
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography

@Composable
fun App() {
	MaterialTheme(
		typography = hankenGroteskTypography
	) {
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
		
		val placeAutocomplete = remember {
			PlaceAutocomplete.create(locationProvider = null)
		}
		
		var selectedLocation by remember { mutableStateOf<SearchResult?>(null) }

		Surface(modifier = Modifier.fillMaxSize()) {
			MapboxMap(
				Modifier.fillMaxSize(),
				mapState = mapState,
				mapViewportState = mapViewportState,
				scaleBar = {},
				logo = {},
				attribution = {},
				compass = {},
				content = {
					if (selectedLocation != null) {
						PointAnnotation(point = selectedLocation?.coordinate!!)
					}
				}
			)
			
			NavHost(navController = navController, startDestination = MainRoute) {
				composable<MainRoute> {
					MainScreen(
						navController,
						searchTextFieldState,
						mapViewportState,
						selectedLocation,
						{ selectedLocation = null }
					)
				}
				composable<SearchRoute> {
					SearchScreen(navController, searchTextFieldState, placeAutocomplete) { result -> selectedLocation = result }
				}
				composable<LocationDetailRoute> { backStackEntry ->
					val route: LocationDetailRoute = backStackEntry.toRoute()
					LocationDetailScreen(navController, route.name, route.description, route.address)
				}
				composable<AddLocationRoute> { backStackEntry ->
					val route: AddLocationRoute = backStackEntry.toRoute()
					AddLocationScreen(navController, route.address)
				}
			}
		}
	}
}