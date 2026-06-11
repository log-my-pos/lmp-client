package dev.pandasystems.logmypos_client.screen.main.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.mapbox.search.autocomplete.PlaceAutocomplete
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.screen.main.location.AddLocationRoute
import dev.pandasystems.logmypos_client.theme.Colors
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.roundToInt

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreenImpl(
        rememberNavController(),
        rememberTextFieldState("Hello World"),
        listOf(
            SearchEntries("123 Main St, London, UK", "Main St", 1340.0, 120.0),
            SearchEntries("456 High St, Manchester, UK", "High St", 120320.0, 1000.0),
        )
    )
}

@Serializable
object SearchRoute

@OptIn(FlowPreview::class)
@Composable
fun SearchScreen(
    navController: NavController,
    searchState: TextFieldState,
    placeAutocomplete: PlaceAutocomplete? = null,
) {
    var suggestions by remember {
        mutableStateOf<List<SearchEntries>>(emptyList())
    }

    LaunchedEffect(searchState.text.toString()) {
        val query = searchState.text.toString().trim()
        if (query.isBlank()) {
            suggestions = emptyList()
            return@LaunchedEffect
        }
        delay(300)

        val response = placeAutocomplete?.suggestions(query = query) ?: return@LaunchedEffect

        response.onValue { results ->
            suggestions = results.map {
                SearchEntries(
                    name = it.formattedAddress ?: it.name,
                    title = it.name,
                    distanceMeters = it.distanceMeters,
                    etaMinutes = it.etaMinutes
                )
            }
        }.onError { error ->
            suggestions = emptyList()
        }
    }

    SearchScreenImpl(
        navController,
        searchState,
        suggestions
    )
}

data class SearchEntries(
    val name: String,
    val title: String,
    val distanceMeters: Double?,
    val etaMinutes: Double?,
)

@Composable
fun SearchScreenImpl(
    navController: NavController,
    searchState: TextFieldState,
    suggestions: List<SearchEntries>
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Colors.background
    ) {
        Column(
            Modifier
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InputField(
                    state = searchState,
                    placeholder = "Search for a place",
                    modifier = Modifier.weight(1f),
                    backgroundColor = Color(0xFFF5F5F5),
                    leftContent = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Icon(
                                imageVector = Tabler.Outline.ArrowLeft,
                                contentDescription = "Back",
                                tint = Colors.text
                            )
                        }
                    },
                    rightContent = {
                        if (searchState.text.isNotEmpty()) {
                            IconButton(
                                onClick = { searchState.edit { delete(0, length) } },
                            ) {
                                Icon(
                                    imageVector = Tabler.Outline.X,
                                    contentDescription = "Clear search",
                                    tint = Colors.text.copy(alpha = 0.5f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }

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
                                navController.navigate(AddLocationRoute(address = suggestion.name))
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
    suggestion: SearchEntries,
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
                    text = suggestion.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Colors.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = suggestion.name,
                    fontSize = 14.sp,
                    color = Colors.text.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (suggestion.distanceMeters != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatDistanceKm(suggestion.distanceMeters),
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