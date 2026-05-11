package dev.pandasystems.logmypos_client.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.ArrayDeque

@Composable
fun NavHost(
	defaultKey: Any,
	compose: @Composable NavHost.() -> Unit
) {
	val navHost = remember { NavHost(defaultKey) }
	
	DisposableEffect(Unit) {
		NavigationManager.controllerStack.push(NavigationController(navHost))
		
		onDispose {
			NavigationManager.controllerStack.pop()
		}
	}

	BackHandler(enabled = navHost.backlogStack.isNotEmpty()) { 
		navHost.currentKey = navHost.backlogStack.pop()
	}
	
	navHost.compose()
}

class NavHost(
	defaultKey: Any,
) {
	internal var currentKey: Any by mutableStateOf(defaultKey)
	internal val backlogStack = ArrayDeque<Any>()
	
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

val localNavController get() = NavigationManager.controllerStack.last()