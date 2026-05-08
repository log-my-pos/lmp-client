package dev.pandasystems.logmypos_client.menus

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.pandasystems.logmypos_client.navigation.Screen

class ProfileScreen : Screen() {
	@Preview
	@Composable
	override fun onContent() {
		MaterialTheme {
			Text("Hello World")
		}
	}
}