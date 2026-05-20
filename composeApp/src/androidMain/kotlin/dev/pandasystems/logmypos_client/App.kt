package dev.pandasystems.logmypos_client

import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.pandasystems.logmypos_client.menus.*
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography
import java.util.*

@Composable
@Preview
fun App() {
	CompositionLocalProvider(LocalRippleConfiguration provides null) {
		MaterialTheme(
			typography = hankenGroteskTypography
		) {
			val navController = rememberNavController()
			val mapNavController = rememberNavController()

			NavHost(navController = navController, startDestination = MapRoute) {
				composable<MapRoute> { MapScreen(navController, mapNavController) }
				composable<ProfileRoute> { backStackEntry ->
					val profile = backStackEntry.toRoute<ProfileRoute>()
					ProfileScreen(navController, UUID.fromString(profile.profileId))
				}
				composable<LocationCreationRoute> { backStackEntry ->
					val route = backStackEntry.toRoute<LocationCreationRoute>()
					LocationCreationScreen(
						mapNavController,
						route.imageUris.map { it.toUri() }
					)
				}
			}
		}
	}
}