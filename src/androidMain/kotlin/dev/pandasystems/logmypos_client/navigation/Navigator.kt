package dev.pandasystems.logmypos_client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun Navigator(
	navHost: NavHostBuilder.() -> Unit
) {
	val controller = remember { NavigationController() }
	
	DisposableEffect(Unit) {
		NavigationManager.controllerStack.push(controller)
		onDispose {
			NavigationManager.controllerStack.pop()
		}
	}
	
	NavHostBuilder().navHost()

	controller.screen.onContent()
}

class NavHostBuilder {
	
}

val localNavController get() = NavigationManager.controllerStack.last()