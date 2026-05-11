package dev.pandasystems.logmypos_client.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*

@Composable
fun NavHost(
	defaultKey: Any,
	compose: @Composable NavHost.() -> Unit
) {
	val navHost = remember { NavHost(defaultKey) }
	val navController = remember(navHost) { NavigationController(navHost) }
	CompositionLocalProvider(LocalNavController provides navController) {
		BackHandler(enabled = navHost.backlogStack.isNotEmpty()) {
			navHost.currentKey = navHost.backlogStack.removeLast()
		}

		navHost.compose()
	}
}

class NavHost(
	defaultKey: Any,
) {
	internal var currentKey: Any by mutableStateOf(defaultKey)
	internal val backlogStack = mutableStateListOf<Any>()
	
	@Composable
	fun <T> Composer(clazz: Class<T>, compose: @Composable () -> Unit) {
		if (clazz == currentKey::class.java) {
			compose()
		}
	}

	@Composable
	inline fun <reified T> Composer(noinline compose: @Composable () -> Unit) {
		Composer(T::class.java, compose)
	}
}