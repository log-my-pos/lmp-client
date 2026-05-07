package dev.pandasystems.logmypos_client.menus

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.serialization.Serializable
import java.util.*

data class ProfileScreen(val id: UUID? = null) : Screen {
	@Composable
	override fun Content() {
	}
}