package dev.pandasystems.logmypos_client.screen.main

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.Search
import com.composables.icons.tabler.outline.X
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.screen.main.search.SearchScreen
import dev.pandasystems.logmypos_client.theme.Colors
import kotlinx.serialization.Serializable

@Serializable
object MainRoute

@Preview
@Composable
private fun PreviewComposite() {
	MainScreen(rememberNavController())
}

@Composable
fun MainScreen(
	rootNavController: NavController
) {
	val searchOpenState = remember { mutableStateOf(false) }
	val searchState = rememberTextFieldState()
	
	Surface(modifier = Modifier.fillMaxSize()) {
		MapComponent()

		Box(modifier = Modifier.fillMaxSize()) {
			Box(Modifier
				.align(Alignment.TopCenter)
				.zIndex(2f)) { SearchBarComponent(searchState, searchOpenState) }

			SearchScreen(searchOpenState, rootNavController, searchState)
		}
	}
}

@Composable
private fun MapComponent() {
//	MapboxMap(
//		Modifier.fillMaxSize(),
//		mapViewportState = rememberMapViewportState {
//			setCameraOptions {
//				zoom(2.0)
//				center(Point.fromLngLat(-98.0, 39.5))
//				pitch(0.0)
//				bearing(0.0)
//			}
//		},
//		scaleBar = {},
//		logo = {},
//		attribution = {},
//		compass = {}
//	)
}

@Composable
private fun SearchBarComponent(
	searchState: TextFieldState,
	openState: MutableState<Boolean>
) {
	val searchbarBackgroundColor by animateColorAsState(
		targetValue = if (openState.value) Color(0xFFE1E1E1) else Colors.background,
		animationSpec = tween(durationMillis = 250)
	)

	InputField(
		state = searchState,
		placeholder = "Enter to search",
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.systemBarsPadding()
			.onFocusChanged {
				if (it.isFocused) {
					openState.value = true
				}
			}
			.dropShadow(
				CircleShape,
				Shadow(
					radius = if (openState.value) 0.dp else 8.dp,
					color = Colors.shadow
				)
			),
		backgroundColor = searchbarBackgroundColor,
		leftContent = { SearchBarLeftContent(openState, searchState) },
		rightContent = { SearchBarRightContent() }
	)
}

@Composable
private fun SearchBarLeftContent(openState: MutableState<Boolean>, searchState: TextFieldState) {
	val focusManager = LocalFocusManager.current

	IconButton(
		modifier = Modifier
			.padding(8.dp)
			.size(40.dp),
		onClick = {
			openState.value = false
			focusManager.clearFocus()
			searchState.clearText()
		}, enabled = openState.value, colors = IconButtonDefaults.iconButtonColors(
			contentColor = Colors.text, disabledContentColor = Colors.text
		)
	) {
		AnimatedContent(
			openState.value, transitionSpec = {
				fadeIn() togetherWith fadeOut()
			}) {
			Icon(
				imageVector = if (it) Tabler.Outline.X else Tabler.Outline.Search,
				contentDescription = "Close search",
				modifier = Modifier
					.fillMaxSize()
					.padding(8.dp)
			)
		}
	}
}

@Composable
private fun SearchBarRightContent() {
//	IconButton(
//		modifier = Modifier
//			.padding(8.dp)
//			.size(40.dp),
//		onClick = {
//			// TODO: Open Profile
//		}, colors = IconButtonDefaults.iconButtonColors(contentColor = Colors.text)
//	) {
//		Icon(
//			imageVector = Tabler.Outline.User,
//			contentDescription = "User profile",
//			modifier = Modifier
//				.fillMaxSize()
//				.padding(8.dp)
//		)
//	}
}