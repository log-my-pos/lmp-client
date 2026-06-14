package dev.pandasystems.logmypos_client.utils

import androidx.compose.runtime.Composable
import dev.pandasystems.logmypos_client.previewModule
import org.koin.compose.KoinIsolatedContext
import org.koin.core.context.startKoin

val previewKoinApp = startKoin {
	modules(previewModule)
}

@Composable
fun SetupPreview(content: @Composable () -> Unit) {
	KoinIsolatedContext(previewKoinApp) {
		content()
	}
}