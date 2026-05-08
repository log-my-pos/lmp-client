package dev.pandasystems.logmypos_client.navigation

import androidx.compose.runtime.Composable

abstract class Screen {
	val currentNavController: NavigationController
		get() = requireNotNull(NavigationManager.controllerStack.peek())
		{ "Couldn't find current navigation controller" }

	@Composable
	abstract fun onContent()
}