package dev.pandasystems.logmypos_client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
fun Navigator(screen: Screen) {
	val controller = remember { NavigationController(screen) }
	
	DisposableEffect(Unit) {
		NavigationManager.controllerStack.push(controller)
		onDispose {
			NavigationManager.controllerStack.pop()
		}
	}

	controller.screen.onContent()
}