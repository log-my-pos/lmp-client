package dev.pandasystems.logmypos_client

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mapbox.search.autocomplete.PlaceAutocomplete
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

		val placeAutocomplete = remember {
			PlaceAutocomplete.create(locationProvider = null)
		}

		NavHost(navController = navController, startDestination = SearchRoute) {
//			composable<MainRoute> { MainScreen(navController) }
			composable<SearchRoute> { SearchScreen(navController, rememberTextFieldState(), placeAutocomplete) }
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