package dev.pandasystems.logmypos_client

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.pandasystems.logmypos_client.menus.MapRoute
import dev.pandasystems.logmypos_client.menus.MapScreen
import dev.pandasystems.logmypos_client.menus.ProfileRoute
import dev.pandasystems.logmypos_client.menus.ProfileScreen
import dev.pandasystems.logmypos_client.theme.hankenGroteskTypography
import java.util.UUID

@Composable
@Preview
fun App() {
	MaterialTheme(
		typography = hankenGroteskTypography
	) {
		val navController = rememberNavController()

		NavHost(navController = navController, startDestination = MapRoute) {
			composable<MapRoute> { MapScreen(navController) }
			composable<ProfileRoute> { backStackEntry ->
				val profile = backStackEntry.toRoute<ProfileRoute>()
				ProfileScreen(navController, UUID.fromString(profile.profileId)) }
		}
	}
}