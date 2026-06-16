package dev.pandasystems.logmypos_client.screen.main

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.*
import com.google.android.gms.location.LocationServices
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.models.GlobalData
import dev.pandasystems.logmypos_client.screen.auth.LoginScreen
import dev.pandasystems.logmypos_client.screen.auth.ProfileScreen
import dev.pandasystems.logmypos_client.screen.location.AddLocationScreen
import dev.pandasystems.logmypos_client.screen.location.AllLocationsScreen
import dev.pandasystems.logmypos_client.screen.search.SearchScreen
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import org.koin.compose.koinInject
import java.io.File

@Preview
@Composable
private fun PreviewMainScreen() = SetupPreviewScreen(MainScreen())

class MainScreen : Screen {
	@Composable
	override fun Content() {
		val globalData = koinInject<GlobalData>()
		val locationService = koinInject<LocationService>()
		val navigator = LocalNavigator.currentOrThrow
		val context = LocalContext.current
		val scope = rememberCoroutineScope()

		var showFabMenu by remember { mutableStateOf(false) }
		var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }

		val cameraLauncher = rememberLauncherForActivityResult(
			contract = ActivityResultContracts.TakePicture(),
			onResult = { success ->
				if (success && photoUri != null) {
					val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
					try {
						fusedLocationClient.lastLocation.addOnSuccessListener { location ->
							if (location != null) {
								navigator.push(AddLocationScreen(location.latitude, location.longitude, initialImageUris = listOf(photoUri.toString())))
							} else {
								// Fallback to 0,0 or show error
								navigator.push(AddLocationScreen(0.0, 0.0, initialImageUris = listOf(photoUri.toString())))
							}
						}
					} catch (e: SecurityException) {
						navigator.push(AddLocationScreen(0.0, 0.0, initialImageUris = listOf(photoUri.toString())))
					}
				}
			}
		)

		val pickerLauncher = rememberLauncherForActivityResult(
			contract = ActivityResultContracts.PickMultipleVisualMedia(),
			onResult = { uris ->
				if (uris.isNotEmpty()) {
					val firstUri = uris.first()
					val coords = getExifLatLong(context, firstUri)
					val allUris = uris.map { it.toString() }
					if (coords != null) {
						navigator.push(AddLocationScreen(coords.first, coords.second, initialImageUris = allUris))
					} else {
						// Fallback to current location or 0,0
						val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
						try {
							fusedLocationClient.lastLocation.addOnSuccessListener { location ->
								if (location != null) {
									navigator.push(AddLocationScreen(location.latitude, location.longitude, initialImageUris = allUris))
								} else {
									navigator.push(AddLocationScreen(0.0, 0.0, initialImageUris = allUris))
								}
							}
						} catch (e: SecurityException) {
							navigator.push(AddLocationScreen(0.0, 0.0, initialImageUris = allUris))
						}
					}
				}
			}
		)

		Box(
			modifier = Modifier
				.fillMaxSize()
				.systemBarsPadding()
		) {
			SearchBar(globalData.searchbarState)

			if (locationService.selectedLocation != null) {
				BackHandler { locationService.clearSelection() }

				Box(Modifier.align(Alignment.BottomCenter)) {
					LocationViewOverlay()
				}
			} else {
				// Floating Action Buttons
				Column(
					modifier = Modifier
						.align(Alignment.BottomEnd)
						.padding(24.dp),
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					AnimatedVisibility(
						visible = showFabMenu,
						enter = expandVertically(),
						exit = shrinkVertically()
					) {
						Column(
							horizontalAlignment = Alignment.End,
							verticalArrangement = Arrangement.spacedBy(16.dp)
						) {
							SmallFab(
								icon = Tabler.Outline.List,
								label = "My Locations",
								onClick = {
									showFabMenu = false
									navigator.push(AllLocationsScreen())
								}
							)
							SmallFab(
								icon = Tabler.Outline.Camera,
								label = "Take Photo",
								onClick = {
									showFabMenu = false
									val photoFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
									photoUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
									cameraLauncher.launch(photoUri!!)
								}
							)
							SmallFab(
								icon = Tabler.Outline.Photo,
								label = "Upload Photo",
								onClick = {
									showFabMenu = false
									pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
								}
							)
						}
					}

					FloatingActionButton(
						onClick = { showFabMenu = !showFabMenu },
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = Color.White,
						shape = CircleShape,
						modifier = Modifier.size(64.dp)
					) {
						Icon(
							imageVector = if (showFabMenu) Tabler.Outline.X else Tabler.Outline.Plus,
							contentDescription = "Add",
							modifier = Modifier.size(32.dp)
						)
					}
				}
			}
		}
	}

	@Composable
	private fun SmallFab(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.spacedBy(12.dp)
		) {
			Surface(
				shape = RoundedCornerShape(8.dp),
				color = Colors.background,
				modifier = Modifier.dropShadow(RoundedCornerShape(8.dp), Shadow(radius = 4.dp, color = Colors.shadow))
			) {
				Text(
					text = label,
					modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
					fontSize = 14.sp,
					fontWeight = FontWeight.Medium
				)
			}
			SmallFloatingActionButton(
				onClick = onClick,
				containerColor = Colors.background,
				contentColor = Colors.text,
				shape = CircleShape
			) {
				Icon(icon, contentDescription = label)
			}
		}
	}

	private fun getExifLatLong(context: android.content.Context, uri: android.net.Uri): Pair<Double, Double>? {
		return try {
			context.contentResolver.openInputStream(uri)?.use { inputStream ->
				val exif = ExifInterface(inputStream)
				val latLong = FloatArray(2)
				if (exif.getLatLong(latLong)) {
					Pair(latLong[0].toDouble(), latLong[1].toDouble())
				} else {
					null
				}
			}
		} catch (e: Exception) {
			null
		}
	}

	@Composable
	private fun SearchBar(searchState: TextFieldState) {
		val navigator = LocalNavigator.currentOrThrow
		val authService = koinInject<AuthService>()
		val isLoggedIn by authService.isLoggedIn.collectAsState()

		InputField(
			state = searchState,
			placeholder = "Search for a place",
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp)
				.onFocusChanged {
					if (it.isFocused) navigator.push(SearchScreen())
				}
				.dropShadow(
					CircleShape,
					Shadow(
						radius = 8.dp,
						color = Colors.shadow
					)
				),
			backgroundColor = Colors.background,
			leftContent = {
				Box(
					modifier = Modifier
						.padding(4.dp)
						.size(36.dp),
				) {
					Icon(
						imageVector = Tabler.Outline.Search,
						contentDescription = "Search icon",
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
					onClick = {
						if (isLoggedIn) {
							navigator.push(ProfileScreen())
						} else {
							navigator.push(LoginScreen())
						}
					}, colors = IconButtonDefaults.iconButtonColors(
						contentColor = if (isLoggedIn) Color(0xFF4CAF50) else Colors.text
					)
				) {
					Icon(
						imageVector = Tabler.Outline.User,
						contentDescription = "User profile",
						modifier = Modifier
							.fillMaxSize()
							.padding(6.dp)
					)
				}
			}
		)
	}
}