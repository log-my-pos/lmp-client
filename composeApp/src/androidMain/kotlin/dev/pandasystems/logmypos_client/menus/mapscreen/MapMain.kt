package dev.pandasystems.logmypos_client.menus.mapscreen

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.*
import dev.pandasystems.logmypos_client.components.Avatar
import dev.pandasystems.logmypos_client.menus.ProfileRoute
import dev.pandasystems.logmypos_client.theme.backgroundLightColor
import dev.pandasystems.logmypos_client.theme.shadowColor
import dev.pandasystems.logmypos_client.theme.textDarkColor
import dev.pandasystems.logmypos_client.theme.textLightColor
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*

@Serializable
data object MapMainOverlayRoute

@Composable
@Preview
fun MapMainOverlay(
	rootNavController: NavController? = null,
	mapNavController: NavHostController? = null
) {
	val searchBarState = remember { mutableStateOf("") }
	val searchWidgetOpenState = remember { mutableStateOf(false) }

	Box(
		modifier = Modifier
			.fillMaxSize()
	) {
		// Overlay
		Box(
			modifier = Modifier
				.systemBarsPadding()
				.padding(16.dp)
				.zIndex(2f)
		) {
			CustomSearchBar(searchBarState, searchWidgetOpenState, rootNavController)
		}

		SearchMenu(searchBarState, searchWidgetOpenState)

		Box(
			modifier = Modifier
				.align(Alignment.BottomEnd)
				.systemBarsPadding()
				.padding(16.dp)
		) {
			NewButton(mapNavController)
		}
	}
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
private fun CustomSearchBar(
	searchBarState: MutableState<String>,
	searchWidgetOpenState: MutableState<Boolean>,
	navController: NavController?
) {
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
			SearchBarDecorationBox(innerTextField, searchBarState, searchWidgetOpenState, fieldShape, navController)
		}
	)
}

@Composable
private fun Modifier.getSearchBarModifier(isSearchWidgetOpen: Boolean, fieldShape: RoundedCornerShape): Modifier {
	val baseModifier = this
		.fillMaxWidth()
		.height(46.dp)

	return if (!isSearchWidgetOpen) {
		baseModifier
			.border(width = 2.dp, color = Color(0xFFFAFAF9), shape = fieldShape)
			.dropShadow(fieldShape, Shadow(8.dp, shadowColor))
	} else {
		baseModifier
	}
}

@Composable
private fun SearchBarDecorationBox(
	innerTextField: @Composable () -> Unit,
	searchBarState: MutableState<String>,
	searchWidgetOpenState: MutableState<Boolean>,
	fieldShape: RoundedCornerShape,
	navController: NavController?
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

		ProfileButton(navController)
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
private fun ProfileButton(
	navController: NavController?
) {
	val interactionSource = remember { MutableInteractionSource() }

	Box(
		modifier = Modifier
			.size(44.dp)
			.clickable(
				interactionSource = interactionSource,
				indication = null,
				onClick = {
					println("Profile button clicked")
					navController?.navigate(ProfileRoute(UUID.randomUUID().toString()))
				}
			),
		contentAlignment = Alignment.Center
	) {
		Avatar(size = 38.dp)
	}
}

@Composable
private fun NewButton(mapNavController: NavHostController?) {
	var isExpanded by remember { mutableStateOf(false) }
	val context = LocalContext.current

	val pickMedia = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickMultipleVisualMedia()
	) { uris ->

	}


	var cameraUri by remember { mutableStateOf<Uri?>(null) }

	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture()
	) { success ->
		if (success) {
			println("Captured image: $cameraUri")
		}
	}

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
	) {
		// Gallery
		AnimatedVisibility(
			visible = isExpanded,
			enter = fadeIn() + slideInVertically { it * 2 },
			exit = fadeOut() + slideOutVertically { it * 2 }
		) {
			IconButton(
				modifier = Modifier
					.size(44.dp),
				onClick = {
					pickMedia.launch(
						PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
					)
				},
				colors = iconButtonColors(
					containerColor = backgroundLightColor,
				)
			) {
				Icon(
					Tabler.Outline.PhotoPlus,
					contentDescription = "",
					modifier = Modifier.size(22.dp)
				)
			}
		}

		Spacer(modifier = Modifier.height(8.dp))

		// Take new photo
		AnimatedVisibility(
			visible = isExpanded,
			enter = fadeIn() + slideInVertically { it },
			exit = fadeOut() + slideOutVertically { it }
		) {
			IconButton(
				modifier = Modifier
					.size(44.dp),
				onClick = {
					val imageFile = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
					val uri = FileProvider.getUriForFile(
						context,
						"${context.packageName}.fileprovider",
						imageFile
					)
					cameraUri = uri
					cameraLauncher.launch(uri)
				},
				colors = iconButtonColors(
					containerColor = backgroundLightColor,
				)
			) {
				Icon(
					Tabler.Outline.CameraPlus,
					contentDescription = "",
					modifier = Modifier.size(22.dp)
				)
			}
		}

		Spacer(modifier = Modifier.height(8.dp))

		// Expand button
		IconButton(
			modifier = Modifier
				.size(50.dp),
			onClick = { isExpanded = !isExpanded },
			colors = iconButtonColors(
				containerColor = animateColorAsState(if (isExpanded) Color(0xFF272726) else backgroundLightColor).value,
				contentColor = animateColorAsState(if (isExpanded) textLightColor else textDarkColor).value
			)
		) {
			Icon(
				Tabler.Outline.Plus,
				contentDescription = "",
				modifier = Modifier
					.rotate(animateFloatAsState(if (isExpanded) 180f + 45f else 0f).value)
					.size(28.dp)
			)
		}
	}
}