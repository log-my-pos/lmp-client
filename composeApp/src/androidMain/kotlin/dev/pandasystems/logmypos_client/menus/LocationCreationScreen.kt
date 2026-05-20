package dev.pandasystems.logmypos_client.menus

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
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
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.CameraPlus
import com.composables.icons.tabler.outline.Minus
import com.composables.icons.tabler.outline.PhotoOff
import com.composables.icons.tabler.outline.PhotoPlus
import dev.pandasystems.logmypos_client.theme.secondaryBackgroundLightColor
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class LocationCreationRoute(
	val imageUris: List<String>,
)

@Composable
fun LocationCreationScreen(
	mapNavController: NavController? = null,
	imageUris: List<Uri> = emptyList(),
) {
	val images = remember { mutableStateListOf(*imageUris.toTypedArray()) }

	Surface(
		modifier = Modifier
			.fillMaxSize()
	) {
		Box(
			modifier = Modifier
				.verticalScroll(rememberScrollState())
				.systemBarsPadding()
				.padding(10.dp)
		) {
			Column(
				modifier = Modifier.fillMaxWidth(),
			) {
				Images(images)

				Spacer(modifier = Modifier.height(10.dp))

				OutlinedTextField(
					modifier = Modifier
						.fillMaxWidth()
						.height(150.dp),
					state = rememberTextFieldState(),
					label = { Text("Description") }
				)
			}

			Button(
				modifier = Modifier
					.align(Alignment.BottomEnd),
				onClick = {}
			) {
				Text("Create")
			}
		}
	}
}

@Composable
private fun Images(images: MutableList<Uri>) {
	val context = LocalContext.current

	val pickMedia = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickMultipleVisualMedia()
	) { uris -> images += uris }

	var cameraUri by remember { mutableStateOf<Uri?>(null) }
	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture()
	) { success ->
		if (success) {
			cameraUri?.let { uri -> images += uri }
		}
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(5.dp),
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.background(secondaryBackgroundLightColor, shape = RoundedCornerShape(20.dp))
				.clip(RoundedCornerShape(20.dp))
		) {
			if (images.isNotEmpty()) {
				LazyVerticalGrid(
					columns = GridCells.Adaptive(100.dp),
					contentPadding = PaddingValues(8.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.heightIn(max = 200.dp)
				) {

					items(images) { uri ->
						ImageEntry(uri, onRemove = { images.remove(uri) })
					}

				}
			} else {
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(20.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					Icon(
						Tabler.Outline.PhotoOff,
						contentDescription = null,
						modifier = Modifier
							.size(52.dp)
					)
					Text("No images", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
				}
			}
		}

		Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
			Button(
				modifier = Modifier
					.weight(1f),
				onClick = {
					pickMedia.launch(
						PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
					)
				},
				contentPadding = PaddingValues(0.dp),
			) {
				Icon(
					Tabler.Outline.PhotoPlus,
					contentDescription = null
				)
				Spacer(Modifier.width(8.dp))
				Text("Add from Gallery")
			}

			Button(
				modifier = Modifier
					.weight(1f),
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
				contentPadding = PaddingValues(0.dp),
			) {
				Icon(
					Tabler.Outline.CameraPlus,
					contentDescription = null
				)
				Spacer(Modifier.width(8.dp))
				Text("Take photo")
			}
		}
	}
}

@Composable
private fun ImageEntry(
	uri: Uri,
	onRemove: () -> Unit,
) {
	Box {
		AsyncImage(
			model = uri,
			contentDescription = null,
			modifier = Modifier
				.fillMaxWidth()
				.aspectRatio(1f)
				.clip(RoundedCornerShape(12.dp))
				.background(Color.Gray),
			contentScale = ContentScale.Crop
		)

		IconButton(
			onClick = onRemove,
			modifier = Modifier
				.align(Alignment.TopEnd)
				.size(32.dp),
			colors = IconButtonDefaults.iconButtonColors(
				containerColor = Color.Transparent,
				contentColor = Color(0xFFB71C1C)
			)
		) {
			Icon(Tabler.Outline.Minus, contentDescription = null)
		}
	}
}

@Preview
@Composable
private fun LocationCreationScreenPreview() {
	val sampleUris = listOf(
		"content://sample/1".toUri(),
		"content://sample/2".toUri(),
		"content://sample/3".toUri(),
		"content://sample/4".toUri(),
	)
	LocationCreationScreen(imageUris = sampleUris)
}