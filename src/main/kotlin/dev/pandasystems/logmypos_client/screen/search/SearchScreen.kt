package dev.pandasystems.logmypos_client.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.MapPin
import com.composables.icons.tabler.outline.X
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.models.location.LocationSearch
import dev.pandasystems.logmypos_client.screen.main.MainRoute
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import java.util.*
import kotlin.math.roundToInt

@Preview
@Composable
private fun SearchScreenPreview() {
	SearchScreen(
		rememberNavController(),
		rememberTextFieldState("Hello World"),
	)
}

@Serializable
object SearchRoute

@OptIn(FlowPreview::class)
@Composable
fun SearchScreen(
	navController: NavController,
	searchState: TextFieldState,
	locationService: LocationService = koinInject(),
) {
	val isPreview = LocalInspectionMode.current
	val focusManager = LocalFocusManager.current
	val keyboardController = LocalSoftwareKeyboardController.current
	val searchFocusRequester = remember { FocusRequester() }
	val coroutineScope = rememberCoroutineScope()

	var suggestions by remember {
		mutableStateOf(
			if (!isPreview)
				emptyList()
			else
				listOf(
					LocationSearch.PREVIEW,
					LocationSearch.PREVIEW,
					LocationSearch.PREVIEW,
					LocationSearch.PREVIEW,
					LocationSearch.PREVIEW,
				)
		)
	}

	LaunchedEffect(Unit) {
		searchFocusRequester.requestFocus()
		keyboardController?.show()
	}

	LaunchedEffect(searchState.text.toString()) {
		if (isPreview) return@LaunchedEffect
		
		val query = searchState.text.toString().trim()
		if (query.isBlank()) {
			suggestions = emptyList()
			return@LaunchedEffect
		}

		suggestions = locationService.queryLocations(query)
	}

	Surface(
		modifier = Modifier.fillMaxSize(),
		color = Colors.background
	) {
		Column(
			Modifier.systemBarsPadding()
		) {
			InputField(
				state = searchState,
				placeholder = "Search for a place",
				modifier = Modifier
					.fillMaxWidth()
					.padding(16.dp)
					.focusRequester(searchFocusRequester),
				backgroundColor = Color(0xFFE6E6E5),
				leftContent = {
					IconButton(
						modifier = Modifier
							.padding(4.dp)
							.size(36.dp),
						onClick = {
							focusManager.clearFocus()
							navController.popBackStack()
						}
					) {
						Icon(
							imageVector = Tabler.Outline.ArrowLeft,
							contentDescription = "Back",
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
						onClick = { searchState.edit { delete(0, length) } },
					) {
						Icon(
							imageVector = Tabler.Outline.X,
							contentDescription = "Clear search",
							modifier = Modifier
								.fillMaxSize()
								.padding(6.dp)
						)
					}
				}
			)

			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(bottom = 16.dp)
			) {
				if (suggestions.isEmpty() && searchState.text.isNotBlank()) {
					item {
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.padding(32.dp),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = "No results found for \"${searchState.text}\"",
								color = Colors.text.copy(alpha = 0.5f),
								fontSize = 16.sp
							)
						}
					}
				} else {
					items(suggestions) { suggestion ->
						SearchSuggestionItem(
							suggestion = suggestion,
							onClick = {
								coroutineScope.launch { locationService.selectedLocation = suggestion.resolve() }
								navController.navigate(MainRoute)
								focusManager.clearFocus()
								keyboardController?.hide()
								searchState.clearText()
							}
						)
						HorizontalDivider(
							modifier = Modifier.padding(horizontal = 16.dp),
							thickness = 0.5.dp,
							color = Color.LightGray.copy(alpha = 0.5f)
						)
					}
				}
			}
		}
	}
}

@Composable
private fun SearchSuggestionItem(
	suggestion: LocationSearch,
	onClick: () -> Unit
) {
	Surface(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth(),
		color = Color.Transparent
	) {
		Row(
			modifier = Modifier
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.background(Color(0xFFF0F0F0), CircleShape),
				contentAlignment = Alignment.Center
			) {
				Icon(
					imageVector = Tabler.Outline.MapPin,
					contentDescription = null,
					tint = Colors.text,
					modifier = Modifier.size(20.dp)
				)
			}

			Spacer(modifier = Modifier.width(16.dp))

			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = suggestion.name,
					fontSize = 16.sp,
					fontWeight = FontWeight.SemiBold,
					color = Colors.text,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				if (suggestion.formattedAddress != null) {
					Text(
						text = suggestion.formattedAddress!!,
						fontSize = 14.sp,
						color = Colors.text.copy(alpha = 0.6f),
						maxLines = 1,
						overflow = TextOverflow.Ellipsis
					)
				}
			}

			if (suggestion.distanceMeters != null) {
				Spacer(modifier = Modifier.width(8.dp))
				Column(horizontalAlignment = Alignment.End) {
					Text(
						text = formatDistanceKm(suggestion.distanceMeters!!),
						fontSize = 14.sp,
						fontWeight = FontWeight.Bold,
						color = Colors.text
					)
					Text(
						text = "km",
						fontSize = 12.sp,
						color = Colors.text.copy(alpha = 0.5f)
					)
				}
			}
		}
	}
}

private fun formatDistanceKm(distanceMeters: Double): String {
	val km = distanceMeters / 1000.0

	return when {
		km > 99 -> "99+"
		km < 10 -> String.format(Locale.UK, "%.1f", km).trimEnd('0').trimEnd('.')
		else -> km.roundToInt().toString()
	}
}