package dev.pandasystems.logmypos_client.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.Search
import com.composables.icons.tabler.outline.User
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.screen.models.SearchModel
import dev.pandasystems.logmypos_client.screen.search.SearchRoute
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import org.koin.compose.koinInject

class MainScreen : Screen {
	@Preview
	@Composable
	override fun Content() {
		val searchModel = koinScreenModel<SearchModel>()
		val locationService = koinInject<LocationService>()

		Box(
			modifier = Modifier
				.fillMaxSize()
				.systemBarsPadding()
		) {
			SearchBar(searchModel.searchbarState)

			if (locationService.selectedLocation != null) {
				BackHandler { locationService.clearSelection() }

				Box(Modifier.align(Alignment.BottomCenter)) {
					LocationViewOverlay(mapViewportState)
				}
			}
		}
	}

	@Composable
	private fun SearchBar(searchState: TextFieldState) {
		val navigator = LocalNavigator.currentOrThrow
		InputField(
			state = searchState,
			placeholder = "Search for a place",
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.onFocusChanged {
					if (it.isFocused) navigator.push(SearchRoute)
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
						// TODO: Open Profile
					}, colors = IconButtonDefaults.iconButtonColors(contentColor = Colors.text)
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