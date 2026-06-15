package dev.pandasystems.logmypos_client.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.Navigator as RealNavigator

@Composable
fun getNavigator(): Navigator {
	val isPreview = LocalInspectionMode.current
	return if (isPreview) FakeNavigator() else NavigatorImpl(LocalNavigator.currentOrThrow)
}

interface Navigator {
	fun navigateTo(screen: Screen)
	fun back()
	fun backUntil(predicate: (Screen) -> Boolean)
}

inline fun <reified SCREEN: Screen> Navigator.backTo() {
	this.backUntil { SCREEN::class.isInstance(it) }
}

class NavigatorImpl(val navigator: RealNavigator) : Navigator {
	override fun navigateTo(screen: Screen) {
		navigator.push(screen)
	}

	override fun back() {
		navigator.pop()
	}

	override fun backUntil(predicate: (Screen) -> Boolean) {
		navigator.popUntil(predicate)
	}
}

class FakeNavigator : Navigator {
	override fun navigateTo(screen: Screen) {}
	override fun back() {}
	override fun backUntil(predicate: (Screen) -> Boolean) {}
}