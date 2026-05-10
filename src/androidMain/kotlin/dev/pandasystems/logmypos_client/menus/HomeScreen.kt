package dev.pandasystems.logmypos_client.menus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.MapSearch
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import dev.pandasystems.logmypos_client.navigation.localNavController

@Preview
@Composable
fun HomeScreen() {
	MaterialTheme {
		Box(modifier = Modifier.fillMaxSize()) {
			MapboxMap(
				modifier = Modifier.fillMaxSize(),
				style = { MapStyle(style = "mapbox://styles/julianmaggio/cmoijn6tp002201sfdm0nab23") },
				mapViewportState = rememberMapViewportState {
					setCameraOptions {
						zoom(2.0)
						center(Point.fromLngLat(-98.0, 39.5))
						pitch(0.0)
						bearing(0.0)
					}
				},
				scaleBar = {},
				logo = {},
				attribution = {},
				compass = {
					Compass(
						modifier = Modifier
							.systemBarsPadding(),
						contentPadding = PaddingValues(16.dp, end = 0.dp, top = 0.dp, bottom = 16.dp),
						alignment = Alignment.BottomStart,
					)
				},
			)

			Box(
				modifier = Modifier
					.align(Alignment.TopCenter)
					.fillMaxSize()
			) {
				var searchBarState by remember { mutableStateOf("") }
				var searchWidgetOpen by remember { mutableStateOf(false) }
				val fieldShape = RoundedCornerShape(100)
				val focusManager = LocalFocusManager.current

				BasicTextField(
					value = searchBarState,
					onValueChange = { searchBarState = it },
					singleLine = true,
					textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
					interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
						LaunchedEffect(interactionSource) {
							interactionSource.interactions.collect {
								if (it is PressInteraction.Release) {
									searchWidgetOpen = true
								}
							}
						}
					},
					modifier = Modifier
						.fillMaxWidth()
						.systemBarsPadding()
						.padding(16.dp)
						.zIndex(2f)
						.height(44.dp)
						.let { baseModifier ->
							if (!searchWidgetOpen) {
								baseModifier
									.border(width = 2.dp, color = Color(0xFFFAFAF9), shape = fieldShape)
									.dropShadow(fieldShape, Shadow(8.dp, Color(0xFF272726).copy(alpha = 0.25f)))
							} else baseModifier
						},
					decorationBox = { innerTextField ->
						Row(
							modifier = Modifier
								.fillMaxSize()
								.background(
									if (searchWidgetOpen) Color(0xFFE6E6E5) else Color(0xFFF5F5F4),
									fieldShape
								)
								.padding(horizontal = 16.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							if (searchWidgetOpen) {
								// Back button
								IconButton(
									onClick = {
										searchWidgetOpen = false
										searchBarState = ""
										focusManager.clearFocus()
									},
									modifier = Modifier
										.width(24.dp)
								) {
									Icon(Tabler.Outline.ArrowLeft, contentDescription = null)
								}
							} else {
								Icon(Tabler.Outline.MapSearch, contentDescription = null)
							}
							Spacer(modifier = Modifier.width(12.dp))

							Box(
								modifier = Modifier.weight(1f),
								contentAlignment = Alignment.CenterStart
							) {
								if (searchBarState.isEmpty()) {
									Text("Search here", color = MaterialTheme.colorScheme.onSurfaceVariant)
								}
								innerTextField()
							}

							Spacer(modifier = Modifier.width(12.dp))
							IconButton(
								onClick = {
									localNavController.navigate(ProfileScreen())
								},
								modifier = Modifier
									.width(24.dp)
							) {
								Icon(Tabler.Outline.ArrowLeft, contentDescription = null)
							}
						}
					}
				)

				AnimatedVisibility(
					visible = searchWidgetOpen,
					enter = fadeIn(),
					exit = fadeOut(),
				) {
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
									headlineContent = { Text("Search result #$index for '$searchBarState'") }
								)
								HorizontalDivider()
							}
						}
					}
				}
			}
		}
	}
}