package dev.pandasystems.logmypos_client.navigation

class NavigationController(val navHost: NavHost) {
	fun navigate(
		newKey: Any,
		shouldAddToBacklog: Boolean = true,
		shouldClearBacklog: Boolean = false
	) {
		if (shouldClearBacklog) {
			NavigationManager.backlogStack.clear()
		}
		if (shouldAddToBacklog) {
			val oldKey = navHost.currentKey
			navHost.backlogStack.push(oldKey)
		}
		navHost.currentKey = newKey
	}
	
	fun navigateBack() {
		if (NavigationManager.backlogStack.isNotEmpty()) {
			NavigationManager.backlogStack.pop().invoke()
		}
	}
}