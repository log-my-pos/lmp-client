package dev.pandasystems.logmypos_client.screen.location

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.work.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.Camera
import com.composables.icons.tabler.outline.MapPin
import com.composables.icons.tabler.outline.Plus
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import dev.pandasystems.logmypos_client.worker.SyncWorker
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream
import kotlin.time.Clock

@Preview
@Composable
private fun PreviewAddLocationScreen() = SetupPreviewScreen(AddLocationScreen(latitude = 0.0, longitude = 0.0))

@OptIn(ExperimentalMaterial3Api::class)
data class AddLocationScreen(
	val latitude: Double,
	val longitude: Double,
	val entryId: Long? = null,
	val initialImageUris: List<String> = emptyList()
) : Screen {
	@Composable
	override fun Content() {
		val locationService: LocationService = koinInject()
		val repository: JournalRepository = koinInject()
        val locationApiService: dev.pandasystems.logmypos_client.api.LocationApiService = koinInject()
        val authService: dev.pandasystems.logmypos_client.services.auth.AuthService = koinInject()
        val isLoggedIn by authService.isLoggedIn.collectAsState()
		val titleState = rememberTextFieldState()
		val descriptionState = rememberTextFieldState()

		var location by remember { mutableStateOf<LocationData?>(null) }
		var selectedImageUris by remember { mutableStateOf<List<android.net.Uri>>(initialImageUris.map { android.net.Uri.parse(it) }) }
		var existingImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }
		val context = LocalContext.current
		val scope = rememberCoroutineScope()

		val navigator = LocalNavigator.currentOrThrow

		val photoPickerLauncher = rememberLauncherForActivityResult(
			contract = ActivityResultContracts.PickMultipleVisualMedia(),
			onResult = { uris -> selectedImageUris = selectedImageUris + uris }
		)

		LaunchedEffect(entryId) {
			if (entryId != null) {
				repository.getEntryById(entryId)?.let { entry ->
					titleState.edit {
						replace(0, length, entry.title)
					}
					descriptionState.edit {
						replace(0, length, entry.description)
					}
					existingImagePaths = entry.imagePaths
				}
			}
		}

		LaunchedEffect(Unit) {
			location = locationService.findLocations(longitude, latitude)
				.firstOrNull()?.resolve()
		}

		Scaffold(
			topBar = {
				OptIn(ExperimentalMaterial3Api::class)
				TopAppBar(
					title = { Text(if (entryId == null) "Add Entry" else "Edit Entry") },
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
                    .padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(16.dp)
			) {
				// Photo Picker Placeholder
				if (selectedImageUris.isNotEmpty() || existingImagePaths.isNotEmpty()) {
					Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)) {
						LazyRow(
							modifier = Modifier.fillMaxSize(),
							horizontalArrangement = Arrangement.spacedBy(8.dp)
						) {
							items(existingImagePaths) { path ->
								AsyncImage(
									model = path,
									contentDescription = null,
									modifier = Modifier
                                        .width(150.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(16.dp)),
									contentScale = ContentScale.Crop
								)
							}
							items(selectedImageUris) { uri ->
								AsyncImage(
									model = uri,
									contentDescription = null,
									modifier = Modifier
                                        .width(150.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(16.dp)),
									contentScale = ContentScale.Crop
								)
							}
							item {
								Box(
									modifier = Modifier
                                        .width(150.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFFF0F0F0))
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
									contentAlignment = Alignment.Center
								) {
									Icon(Tabler.Outline.Plus, contentDescription = "Add more", tint = Color.Gray)
								}
							}
						}
					}
				} else {
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
							location.address?.formattedAddress
								?: location.coordinate.let { point ->
									"${point.latitude()}, ${point.longitude()} | ${location.name}"
								}
						} ?: "$latitude, $longitude",
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
							val newImagePaths = selectedImageUris.mapNotNull { uri ->
								saveImageToInternalStorage(context, uri)
							}
							val allImagePaths = existingImagePaths + newImagePaths

                            var synced = false
                            if (isLoggedIn) {
                                val response = locationApiService.createLocation(
                                    title = titleState.text.toString(),
                                    description = descriptionState.text.toString(),
                                    latitude = latitude,
                                    longitude = longitude
                                )
                                synced = response != null
                            }

							val entry = JournalEntry(
								id = entryId ?: 0L,
								title = titleState.text.toString(),
								description = descriptionState.text.toString(),
								latitude = latitude,
								longitude = longitude,
								address = location?.address?.formattedAddress ?: location?.name,
								date = Clock.System.now().toEpochMilliseconds(),
                                imagePaths = allImagePaths,
                                isSynced = synced
							)

							if (entryId == null) {
								repository.insert(entry)
							} else {
								repository.update(entry)
							}

                            if (isLoggedIn && !synced) {
                                triggerSync(context)
                            }

							locationService.clearSelection()
							navigator.pop()
						}
					},
					modifier = Modifier.fillMaxWidth(),
					shape = RoundedCornerShape(12.dp),
					contentPadding = PaddingValues(16.dp)
				) {
					Text(if (entryId == null) "Save Entry" else "Update Entry", fontSize = 18.sp)
				}
			}
		}
	}

    @Composable
    fun AsyncImage(model: String, contentDescription: Nothing?, modifier: Modifier, contentScale: ContentScale) {
        TODO("Not yet implemented")
    }

    private fun triggerSync(context: android.content.Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "LocationSync",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            syncRequest
        )
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
}
