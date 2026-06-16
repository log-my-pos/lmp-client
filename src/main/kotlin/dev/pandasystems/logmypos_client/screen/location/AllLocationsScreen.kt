package dev.pandasystems.logmypos_client.screen.location

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.MapPin
import com.composables.icons.tabler.outline.Search
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import org.koin.compose.koinInject

@Preview
@Composable
private fun PreviewAllLocationsScreen() = SetupPreviewScreen(AllLocationsScreen())

@OptIn(ExperimentalMaterial3Api::class)
class AllLocationsScreen : Screen {
    @Composable
    override fun Content() {
        val repository = koinInject<JournalRepository>()
        val navigator = LocalNavigator.currentOrThrow
        val entries by repository.allEntries.collectAsState(emptyList())
        val searchState = rememberTextFieldState()

        val filteredEntries = remember(entries, searchState.text) {
            val query = searchState.text.toString().lowercase()
            if (query.isBlank()) {
                entries
            } else {
                entries.filter {
                    it.title.lowercase().contains(query) || (it.address?.lowercase()?.contains(query) ?: false)
                }
            }
        }

        Scaffold(
            topBar = {
                OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("My Locations") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Tabler.Outline.ArrowLeft, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                InputField(
                    state = searchState,
                    placeholder = "Search your locations",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = Color(0xFFF0F0F0),
                    leftContent = {
                        Icon(
                            Tabler.Outline.Search,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp).size(20.dp)
                        )
                    }
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredEntries) { entry ->
                        LocationItem(entry = entry) {
                            navigator.push(LocationDetailScreen(entry.id))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LocationItem(entry: JournalEntry, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (entry.imagePath != null) {
                    AsyncImage(
                        model = entry.imagePath,
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Tabler.Outline.MapPin,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = entry.address ?: "No address",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
