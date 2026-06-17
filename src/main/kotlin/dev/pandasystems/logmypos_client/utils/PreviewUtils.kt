package dev.pandasystems.logmypos_client.utils

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import dev.pandasystems.logmypos_client.App
import dev.pandasystems.logmypos_client.previewModule
import org.koin.compose.KoinIsolatedContext
import org.koin.core.context.startKoin

val previewKoinApp = startKoin {
	modules(previewModule)
}

@Composable
fun SetupPreview(content: @Composable () -> Unit) {
	App {
		KoinIsolatedContext(previewKoinApp) {
			content()
		}
	}
}

@Composable
fun SetupPreviewScreen(screen: Screen) = SetupPreview {
	Navigator(screen)
}