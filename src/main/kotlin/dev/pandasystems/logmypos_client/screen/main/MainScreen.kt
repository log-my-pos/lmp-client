package dev.pandasystems.logmypos_client.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.Search
import com.composables.icons.tabler.outline.User
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.models.GlobalData
import dev.pandasystems.logmypos_client.screen.auth.LoginScreen
import dev.pandasystems.logmypos_client.screen.auth.ProfileScreen
import dev.pandasystems.logmypos_client.screen.search.SearchScreen
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import org.koin.compose.koinInject

@Preview
@Composable
private fun PreviewMainScreen() = SetupPreviewScreen(MainScreen())

class MainScreen : Screen {
	@Composable
	override fun Content() {
		val globalData = koinInject<GlobalData>()
		val locationService = koinInject<LocationService>()

		Box(
			modifier = Modifier
				.fillMaxSize()
				.systemBarsPadding()
		) {
			SearchBar(globalData.searchbarState)

			if (locationService.selectedLocation != null) {
				BackHandler { locationService.clearSelection() }

				Box(Modifier.align(Alignment.BottomCenter)) {
					LocationViewOverlay()
				}
			}
		}
	}

	@Composable
	private fun SearchBar(searchState: TextFieldState) {
		val navigator = LocalNavigator.currentOrThrow
		val authService = koinInject<AuthService>()
		val isLoggedIn by authService.isLoggedIn.collectAsState()

		InputField(
			state = searchState,
			placeholder = "Search for a place",
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.onFocusChanged {
					if (it.isFocused) navigator.push(SearchScreen())
				}
				.dropShadow(
					CircleShape,
					Shadow(
						radius = 8.dp,
						color = Colors.shadow
					)
				),
			backgroundColor = Colors.background,
			leftContent = {
				Box(
					modifier = Modifier
						.padding(4.dp)
						.size(36.dp),
				) {
					Icon(
						imageVector = Tabler.Outline.Search,
						contentDescription = "Search icon",
						modifier = Modifier
							.fillMaxSize()
							.padding(6.dp)
					)
				}
			},
			rightContent = {
				IconButton(
					modifier = Modifier
						.padding(4.dp)
						.size(36.dp),
					onClick = {
						if (isLoggedIn) {
							navigator.push(ProfileScreen())
						} else {
							navigator.push(LoginScreen())
						}
					}, colors = IconButtonDefaults.iconButtonColors(
						contentColor = if (isLoggedIn) Color(0xFF4CAF50) else Colors.text
					)
				) {
					Icon(
						imageVector = Tabler.Outline.User,
						contentDescription = "User profile",
						modifier = Modifier
							.fillMaxSize()
							.padding(6.dp)
					)
				}
			}
		)
	}
}