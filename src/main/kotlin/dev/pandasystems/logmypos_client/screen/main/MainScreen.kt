package dev.pandasystems.logmypos_client.screen.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.Search
import com.composables.icons.tabler.outline.User
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.models.search.SearchResult
import dev.pandasystems.logmypos_client.screen.main.search.SearchRoute
import dev.pandasystems.logmypos_client.theme.Colors
import kotlinx.serialization.Serializable

@Preview
@Composable
private fun PreviewComposite() {
	MainScreen(
		rememberNavController(),
		rememberTextFieldState(),
		rememberMapViewportState(),
		null,
		{}
	)
}

@Serializable
object MainRoute

@Composable
fun MainScreen(
	rootNavController: NavController,
	searchState: TextFieldState,
	mapViewportState: MapViewportState,
	selectedLocation: SearchResult?,
	closeSelection: () -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
			.systemBarsPadding()
	) {
		SearchBar(rootNavController, searchState)
		
		if (selectedLocation != null) {
			BackHandler { closeSelection() }
			
			Box(Modifier.align(Alignment.BottomCenter)) {
				LocationViewOverlay(selectedLocation, rootNavController, mapViewportState)
			}
		}
	}
}

@Composable
private fun SearchBar(rootNavController: NavController, searchState: TextFieldState) {
	InputField(
		state = searchState,
		placeholder = "Search for a place",
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.onFocusChanged {
				if (it.isFocused) rootNavController.navigate(SearchRoute)
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