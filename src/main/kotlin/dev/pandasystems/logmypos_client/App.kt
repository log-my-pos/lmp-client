package dev.pandasystems.logmypos_client

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.search.autocomplete.PlaceAutocomplete
import dev.pandasystems.logmypos_client.screen.main.MainRoute
import dev.pandasystems.logmypos_client.screen.main.MainScreen
import dev.pandasystems.logmypos_client.screen.main.location.AddLocationRoute
import dev.pandasystems.logmypos_client.screen.main.location.AddLocationScreen
import dev.pandasystems.logmypos_client.screen.main.location.LocationDetailRoute
import dev.pandasystems.logmypos_client.screen.main.location.LocationDetailScreen
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

		NavHost(navController = navController, startDestination = MainRoute) {
			composable<MainRoute> { MainScreen(navController, searchTextFieldState, mapState, mapViewportState) }
			composable<SearchRoute> {
				SearchScreen(
					navController,
					searchTextFieldState,
					placeAutocomplete,
					mapViewportState
				)
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