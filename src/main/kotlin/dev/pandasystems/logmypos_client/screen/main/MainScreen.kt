package dev.pandasystems.logmypos_client.screen.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dev.pandasystems.logmypos_client.LocalSearchBarProvider
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.screen.auth.LoginScreen
import dev.pandasystems.logmypos_client.screen.auth.ProfileScreen
import dev.pandasystems.logmypos_client.screen.location.AllLocationsScreen
import dev.pandasystems.logmypos_client.screen.location.JournalEntryScreen
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
        val locationService = koinInject<LocationService>()
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        var showFabMenu by remember { mutableStateOf(false) }
        var photoUri by remember { mutableStateOf<android.net.Uri?>(null) }

        val navigateWithLocation = { uris: List<String> ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                navigator.push(JournalEntryScreen(latitude = 0.0, longitude = 0.0, initialImageUris = uris))
            } else {
                try {
                    fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                        val location = if (task.isSuccessful) task.result else null
                        if (location != null) {
                            navigator.push(
                                JournalEntryScreen(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    initialImageUris = uris
                                )
                            )
                        } else {
                            val cts = CancellationTokenSource()
                            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                                .addOnCompleteListener { currentTask ->
                                    val currentLocation = if (currentTask.isSuccessful) currentTask.result else null
                                    if (currentLocation != null) {
                                        navigator.push(
                                            JournalEntryScreen(
                                                latitude = currentLocation.latitude,
                                                longitude = currentLocation.longitude,
                                                initialImageUris = uris
                                            )
                                        )
                                    } else {
                                        navigator.push(
                                            JournalEntryScreen(
                                                latitude = 0.0,
                                                longitude = 0.0,
                                                initialImageUris = uris
                                            )
                                        )
                                    }
                                }
                        }
                    }
                } catch (e: SecurityException) {
                    navigator.push(JournalEntryScreen(latitude = 0.0, longitude = 0.0, initialImageUris = uris))
                }
            }
        }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success && photoUri != null) {
                    navigateWithLocation(listOf(photoUri.toString()))
                }
            }
        )

        val pickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                if (uris.isNotEmpty()) {
                    val firstUri = uris.first()
                    val coords = getExifLatLong(context, firstUri)
                    val allUris = uris.map { it.toString() }
                    if (coords != null) {
                        navigator.push(
                            JournalEntryScreen(
                                latitude = coords.first,
                                longitude = coords.second,
                                initialImageUris = allUris
                            )
                        )
                    } else {
                        navigator.push(JournalEntryScreen(latitude = 0.0, longitude = 0.0, initialImageUris = allUris))
                    }
                }
            }
        )

        val takePhotoPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                if (permissions[Manifest.permission.CAMERA] == true) {
                    val photoFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    photoUri = uri
                    cameraLauncher.launch(uri)
                }
            }
        )

        val uploadPhotoPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { _ ->
                pickerLauncher.launch("image/*")
            }
        )

        Box(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            SearchBar()

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
                                    val hasCameraPermission =
                                        context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                    val hasLocationPermission =
                                        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                                    if (hasCameraPermission && hasLocationPermission) {
                                        val photoFile =
                                            File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                                        photoUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            photoFile
                                        )
                                        cameraLauncher.launch(photoUri!!)
                                    } else {
                                        takePhotoPermissionLauncher.launch(
                                            arrayOf(
                                                Manifest.permission.CAMERA,
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                            )
                                        )
                                    }
                                }
                            )
                            SmallFab(
                                icon = Tabler.Outline.Photo,
                                label = "Upload Photo",
                                onClick = {
                                    showFabMenu = false
                                    val hasLocationPermission =
                                        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                                    val hasMediaLocationPermission =
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            context.checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED
                                        } else true

                                    val hasStoragePermission =
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                                        } else {
                                            context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                        }

                                    if (hasLocationPermission && hasMediaLocationPermission && hasStoragePermission) {
                                        pickerLauncher.launch("image/*")
                                    } else {
                                        val permissions = mutableListOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            permissions.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
                                        }
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                                        } else {
                                            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                                        }
                                        uploadPhotoPermissionLauncher.launch(permissions.toTypedArray())
                                    }
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
            val photoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    MediaStore.setRequireOriginal(uri)
                } catch (e: Exception) {
                    uri
                }
            } else {
                uri
            }
            // Try FileDescriptor first as it is more reliable for ExifInterface
            val coords = context.contentResolver.openFileDescriptor(photoUri, "r")?.use { pfd ->
                val exif = ExifInterface(pfd.fileDescriptor)
                exif.latLong?.let {
                    Pair(it[0], it[1])
                }
            }
            if (coords != null) return coords

            // Fallback to InputStream
            context.contentResolver.openInputStream(photoUri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                exif.latLong?.let {
                    Pair(it[0], it[1])
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    private fun SearchBar() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = koinInject<AuthService>()
        val isLoggedIn by authService.isLoggedIn.collectAsState()

        InputField(
            state = LocalSearchBarProvider.current,
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