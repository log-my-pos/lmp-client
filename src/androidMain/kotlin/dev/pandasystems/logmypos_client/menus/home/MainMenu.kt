package dev.pandasystems.logmypos_client.menus.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.MapSearch
import dev.pandasystems.logmypos_client.components.Avatar
import dev.pandasystems.logmypos_client.menus.Profile
import dev.pandasystems.logmypos_client.navigation.LocalNavController
import java.util.*


object MainHomeMenu

@Composable
@Preview
fun MainHomeMenu() {
	val searchBarState = remember { mutableStateOf("") }
	val searchWidgetOpenState = remember { mutableStateOf(false) }

	CustomSearchBar(searchBarState, searchWidgetOpenState)
	SearchMenu(searchBarState, searchWidgetOpenState)
}


@Composable
private fun SearchMenu(
	searchBarState: MutableState<String>,
	searchWidgetOpenState: MutableState<Boolean>
) {
	AnimatedVisibility(
		visible = searchWidgetOpenState.value,
		enter = fadeIn(),
		exit = fadeOut(),
	) {
		BackHandler {
			searchWidgetOpenState.value = false
		}

		Surface(
			modifier = Modifier
				.fillMaxSize()
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.systemBarsPadding()
					.padding(top = 70.dp),
			) {
				// Populate this with your real list of items later
				items(20) { index ->
					ListItem(
						headlineContent = { Text("Search result #$index for '${searchBarState.value}'") },
					)
					HorizontalDivider()
				}
			}
		}
	}
}

@Composable
private fun CustomSearchBar(searchBarState: MutableState<String>, searchWidgetOpenState: MutableState<Boolean>) {
	val fieldShape = RoundedCornerShape(100)
	val interactionSource = remember { MutableInteractionSource() }

	LaunchedEffect(interactionSource) {
		interactionSource.interactions.collect { interaction ->
			if (interaction is PressInteraction.Release) {
				searchWidgetOpenState.value = true
			}
		}
	}

	BasicTextField(
		value = searchBarState.value,
		onValueChange = { searchBarState.value = it },
		singleLine = true,
		textStyle = TextStyle(
			color = Color(0xFF272726),
			fontSize = 18.sp,
			fontWeight = FontWeight.Medium,
		),
		interactionSource = interactionSource,
		modifier = Modifier.getSearchBarModifier(searchWidgetOpenState.value, fieldShape),
		decorationBox = { innerTextField ->
			SearchBarDecorationBox(innerTextField, searchBarState, searchWidgetOpenState, fieldShape)
		}
	)
}

@Composable
private fun Modifier.getSearchBarModifier(isSearchWidgetOpen: Boolean, fieldShape: RoundedCornerShape): Modifier {
	val baseModifier = this
		.fillMaxWidth()
		.systemBarsPadding()
		.padding(16.dp)
		.zIndex(2f)
		.height(46.dp)

	return if (!isSearchWidgetOpen) {
		baseModifier
			.border(width = 2.dp, color = Color(0xFFFAFAF9), shape = fieldShape)
			.dropShadow(fieldShape, Shadow(8.dp, Color(0xFF272726).copy(alpha = 0.25f)))
	} else {
		baseModifier
	}
}

@Composable
private fun SearchBarDecorationBox(
	innerTextField: @Composable () -> Unit,
	searchBarState: MutableState<String>,
	searchWidgetOpenState: MutableState<Boolean>,
	fieldShape: RoundedCornerShape
) {
	Row(
		modifier = Modifier
			.fillMaxSize()
			.background(
				if (searchWidgetOpenState.value) Color(0xFFE6E6E5) else Color(0xFFF5F5F4),
				fieldShape
			)
			.padding(2.dp),
		verticalAlignment = Alignment.CenterVertically
	) {
		BackButtonOrSearchIcon(searchWidgetOpenState, searchBarState)

		Box(
			modifier = Modifier.weight(1f),
			contentAlignment = Alignment.CenterStart
		) {
			if (searchBarState.value.isEmpty()) {
				Text(
					"Search here",
					color = Color(0xFF545452),
					fontSize = 18.sp,
					fontWeight = FontWeight.Medium
				)
			}
			innerTextField()
		}

		ProfileButton(searchWidgetOpenState)
	}
}

@Composable
private fun BackButtonOrSearchIcon(searchWidgetOpenState: MutableState<Boolean>, searchBarState: MutableState<String>) {
	val focusManager = LocalFocusManager.current
	val interactionSource = remember { MutableInteractionSource() }

	if (searchWidgetOpenState.value) {
		BackButton(searchWidgetOpenState, searchBarState, focusManager, interactionSource)
	} else {
		SearchIcon()
	}
}

@Composable
private fun BackButton(
	searchWidgetOpenState: MutableState<Boolean>,
	searchBarState: MutableState<String>,
	focusManager: FocusManager,
	interactionSource: MutableInteractionSource
) {
	Box(
		modifier = Modifier
			.size(44.dp)
			.clickable(
				interactionSource = interactionSource,
				indication = null,
				onClick = {
					searchWidgetOpenState.value = false
					searchBarState.value = ""
					focusManager.clearFocus()
				}
			),
		contentAlignment = Alignment.Center
	) {
		Icon(
			Tabler.Outline.ArrowLeft,
			contentDescription = null,
			tint = Color(0xFF272726)
		)
	}
}

@Composable
private fun SearchIcon() {
	Box(
		modifier = Modifier.size(44.dp),
		contentAlignment = Alignment.Center
	) {
		Icon(
			Tabler.Outline.MapSearch,
			contentDescription = null,
			tint = Color(0xFF545452),
		)
	}
}

@Composable
private fun ProfileButton(searchWidgetOpenState: MutableState<Boolean>) {
	val navController = LocalNavController.current
	val interactionSource = remember { MutableInteractionSource() }

	Box(
		modifier = Modifier
			.size(44.dp)
			.clickable(
				enabled = searchWidgetOpenState.value,
				interactionSource = interactionSource,
				indication = null,
				onClick = {
					navController?.navigate(Profile(UUID.randomUUID()))
				}
			),
		contentAlignment = Alignment.Center
	) {
		Avatar(size = 38.dp)
	}
}