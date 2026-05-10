package dev.pandasystems.logmypos_client.navigation

import androidx.compose.runtime.mutableStateOf

class NavigationController(
	initialScreen: Screen
) {
	private val _screen = mutableStateOf(initialScreen)
	val screen: Screen
		get() = _screen.value

	fun navigate(
		newScreen: Screen,
		shouldAddToBacklog: Boolean = true,
		shouldClearBacklog: Boolean = false
	) {
		if (shouldClearBacklog) {
			NavigationManager.backlogStack.clear()
		}
		if (shouldAddToBacklog) {
			val oldScreen = screen
			NavigationManager.backlogStack.push { _screen.value = oldScreen }
		}
		_screen.value = newScreen
	}
	
	fun navigateBack() {
		if (NavigationManager.backlogStack.isNotEmpty()) {
			NavigationManager.backlogStack.pop().invoke()
		}
	}
}