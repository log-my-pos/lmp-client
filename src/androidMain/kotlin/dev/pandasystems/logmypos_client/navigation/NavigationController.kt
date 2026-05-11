package dev.pandasystems.logmypos_client.navigation

import java.util.ArrayDeque

class NavigationController(val navHost: NavHost) {
	fun navigate(
		newKey: Any,
		shouldAddToBacklog: Boolean = true,
		shouldClearBacklog: Boolean = false
	) {
		if (shouldClearBacklog) {
			navHost.backlogStack.clear()
		}
		if (shouldAddToBacklog) {
			val oldKey = navHost.currentKey
			navHost.backlogStack.add(oldKey)
		}
		navHost.currentKey = newKey
	}
	
	fun navigateBack() {
		if (navHost.backlogStack.isNotEmpty()) {
			navHost.currentKey = navHost.backlogStack.removeLast()
		}
	}
}

internal val controllerStack = ArrayDeque<NavigationController>()

val localNavController get() = controllerStack.last()