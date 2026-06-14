package dev.pandasystems.logmypos_client.screen.main.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.Camera
import com.composables.icons.tabler.outline.MapPin
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.models.search.SearchSuggestion
import dev.pandasystems.logmypos_client.models.search.toSearchSuggestion
import dev.pandasystems.logmypos_client.repository.JournalRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import kotlin.time.Clock

@Preview
@Composable
private fun PreviewAddLocationScreen() {
	AddLocationScreen(
		route = AddLocationRoute(latitude = 0.0, longitude = 0.0),
		navController = rememberNavController(),
		placeAutocomplete = null,
		titleState = rememberTextFieldState(),
		descriptionState = rememberTextFieldState(),
	)
}

@Serializable
data class AddLocationRoute(
	val longitude: Double,
	val latitude: Double,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
	route: AddLocationRoute,
	navController: NavController,
	placeAutocomplete: PlaceAutocomplete?,
	titleState: TextFieldState,
	descriptionState: TextFieldState,
	repository: JournalRepository? = null,
) {
	var location by remember { mutableStateOf<SearchSuggestion?>(null) }
	var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
	val context = LocalContext.current
	val scope = rememberCoroutineScope()

	val photoPickerLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = { uri -> selectedImageUri = uri }
	)

	LaunchedEffect(Unit) {
		val response =
			placeAutocomplete?.reverse(Point.fromLngLat(route.longitude, route.latitude)) ?: return@LaunchedEffect

		response.onValue { suggestions ->
			if (suggestions.isNotEmpty()) {
				val suggestion = suggestions.first()
				location = suggestion.toSearchSuggestion()
			}
		}.onError {
			// Handle error
		}
	}

	Scaffold(
		topBar = {
			OptIn(ExperimentalMaterial3Api::class)
			TopAppBar(
				title = { Text("Add Entry") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
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
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			// Photo Picker Placeholder
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(200.dp)
					.clip(RoundedCornerShape(16.dp))
					.background(Color(0xFFF0F0F0))
					.clickable {
						photoPickerLauncher.launch(
							PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
						)
					},
				contentAlignment = Alignment.Center
			) {
				if (selectedImageUri != null) {
					AsyncImage(
						model = selectedImageUri,
						contentDescription = "Selected image",
						modifier = Modifier.fillMaxSize(),
						contentScale = ContentScale.Crop
					)
				} else {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Icon(
							imageVector = Tabler.Outline.Camera,
							contentDescription = null,
							modifier = Modifier.size(48.dp),
							tint = Color.Gray
						)
						Text("Add Photo", color = Color.Gray)
					}
				}
			}

			Text(
				text = "Location Information",
				fontSize = 18.sp,
				fontWeight = FontWeight.Bold
			)

			Row(verticalAlignment = Alignment.CenterVertically) {
				Icon(
					imageVector = Tabler.Outline.MapPin,
					contentDescription = null,
					modifier = Modifier.size(20.dp),
					tint = MaterialTheme.colorScheme.primary
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text(
					text = location?.let { location ->
						location.formattedAddress
							?: location.coordinate?.let { point ->
								"${point.latitude()}, ${point.longitude()} | ${location.name}"
							} ?: location.name
					} ?: "${route.latitude}, ${route.longitude}",
					fontSize = 16.sp
				)
			}

			Text(
				text = "Title",
				fontSize = 14.sp,
				fontWeight = FontWeight.Medium
			)
			InputField(
				state = titleState,
				placeholder = "Give your memory a name",
				modifier = Modifier.fillMaxWidth(),
				backgroundColor = Color(0xFFF0F0F0)
			)

			Text(
				text = "Description",
				fontSize = 14.sp,
				fontWeight = FontWeight.Medium
			)
			InputField(
				state = descriptionState,
				placeholder = "What happened here?",
				modifier = Modifier
					.fillMaxWidth()
					.heightIn(min = 100.dp),
				backgroundColor = Color(0xFFF0F0F0),
				shape = RoundedCornerShape(16.dp)
			)

			Spacer(modifier = Modifier.weight(1f))

			Button(
				onClick = {
					scope.launch {
						val imagePath = selectedImageUri?.let { uri ->
							saveImageToInternalStorage(context, uri)
						}

						val entry = JournalEntry(
							title = titleState.text.toString(),
							description = descriptionState.text.toString(),
							latitude = route.latitude,
							longitude = route.longitude,
							address = location?.formattedAddress ?: location?.name,
							date = Clock.System.now().toEpochMilliseconds(),
							imagePath = imagePath
						)

						repository?.insert(entry)
						navController.popBackStack()
					}
				},
				modifier = Modifier.fillMaxWidth(),
				shape = RoundedCornerShape(12.dp),
				contentPadding = PaddingValues(16.dp)
			) {
				Text("Save Entry", fontSize = 18.sp)
			}
		}
	}
}

private fun saveImageToInternalStorage(context: android.content.Context, uri: android.net.Uri): String? {
	return try {
		val inputStream = context.contentResolver.openInputStream(uri) ?: return null
		val fileName = "image_${System.currentTimeMillis()}.jpg"
		val file = File(context.filesDir, fileName)
		val outputStream = FileOutputStream(file)
		inputStream.use { input ->
			outputStream.use { output ->
				input.copyTo(output)
			}
		}
		file.absolutePath
	} catch (e: Exception) {
		e.printStackTrace()
		null
	}
}
