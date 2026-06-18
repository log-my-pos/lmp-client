package dev.pandasystems.logmypos_client.screen.location

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.*
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.blur.materials.CupertinoMaterials
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.pandasystems.logmypos_client.LocalMapViewportStateProvider
import dev.pandasystems.logmypos_client.api.LocationApiService
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.models.location.LocationData
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import dev.pandasystems.logmypos_client.utils.SyncUtils
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
private fun PreviewBaseJournalScreen() = SetupPreviewScreen(JournalEntryScreen(0L))

class JournalEntryScreen(
    val entryId: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val initialImageUris: List<String> = emptyList()
) : Screen {
    @Composable
    override fun Content() {
        val repository = koinInject<JournalRepository>()
        val locationService: LocationService = koinInject()
        val locationApiService: LocationApiService = koinInject()
        val authService: AuthService = koinInject()
        val isLoggedIn by authService.isLoggedIn.collectAsState()

        var entry by remember { mutableStateOf<JournalEntry?>(null) }
        var isLoading by remember { mutableStateOf(entryId != null) }

        var showPhotoViewer by remember { mutableStateOf(false) }
        var showDetailsModal by remember { mutableStateOf(false) }

        var isEditingTitle by remember { mutableStateOf(false) }
        var editedTitle by remember { mutableStateOf("") }
        var isEditingDescription by remember { mutableStateOf(false) }
        var editedDescription by remember { mutableStateOf("") }
        var editedImagePaths by remember { mutableStateOf<List<String>>(emptyList()) }
        var currentLocation by remember { mutableStateOf<LocationData?>(null) }

        var showDeleteEntryConfirmation by remember { mutableStateOf(false) }
        var showDeletePhotoConfirmation by remember { mutableStateOf(false) }

        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val titleFocusRequester = remember { FocusRequester() }
        val descriptionFocusRequester = remember { FocusRequester() }
        val modalTitleFocusRequester = remember { FocusRequester() }

        val mapViewportState = LocalMapViewportStateProvider.current

        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris ->
                scope.launch {
                    val newImagePaths = uris.mapNotNull { uri ->
                        saveImageToInternalStorage(context, uri)
                    }
                    editedImagePaths = editedImagePaths + newImagePaths
                }
            }
        )

        LaunchedEffect(entryId) {
            if (entryId != null) {
                entry = repository.getEntryById(entryId)
                if (entry != null) {
                    editedTitle = entry!!.title
                    editedDescription = entry!!.description
                    editedImagePaths = entry!!.imagePaths
                    isLoading = false

                    mapViewportState.flyTo(cameraOptions {
                        center(Point.fromLngLat(entry!!.longitude, entry!!.latitude))
                        zoom(15.0)
                    })
                }
            } else if (latitude != null && longitude != null) {
                currentLocation = locationService.findLocations(longitude, latitude).firstOrNull()?.resolve()

                val newEntry = JournalEntry(
                    title = "",
                    description = "",
                    latitude = latitude,
                    longitude = longitude,
                    address = currentLocation?.address?.formattedAddress ?: currentLocation?.name,
                    date = kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                        .toLocalDateTime(TimeZone.currentSystemDefault()),
                    imagePaths = emptyList()
                )
                entry = newEntry
                editedTitle = ""
                editedDescription = ""
                editedImagePaths =
                    initialImageUris.mapNotNull { saveImageToInternalStorage(context, android.net.Uri.parse(it)) }
                isLoading = false

                mapViewportState.flyTo(cameraOptions {
                    center(Point.fromLngLat(longitude, latitude))
                    zoom(15.0)
                })
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        onClick = { navigator.pop() },
                        shape = CircleShape,
                        color = Colors.background,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Tabler.Outline.ArrowLeft, contentDescription = "Back", modifier = Modifier.size(20.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isEditingTitle) {
                            BasicTextField(
                                value = editedTitle,
                                onValueChange = { editedTitle = it },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Colors.text
                                ),
                                modifier = Modifier
                                    .focusRequester(titleFocusRequester)
                                    .fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    isEditingTitle = false
                                    focusManager.clearFocus()
                                }),
                                singleLine = true
                            )
                            LaunchedEffect(Unit) {
                                titleFocusRequester.requestFocus()
                            }
                        } else {
                            Text(
                                editedTitle.ifBlank { if (entryId == null) "New Entry" else "No Title" },
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (editedTitle.isBlank()) Colors.text.copy(alpha = 0.5f) else Colors.text
                                ),
                                modifier = Modifier.clickable { isEditingTitle = true },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    if (entryId != null) {
                        Surface(
                            onClick = { showDeleteEntryConfirmation = true },
                            shape = CircleShape,
                            color = Colors.background,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Tabler.Outline.Trash,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(Modifier.size(40.dp))
                    }
                }

                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(-11f, onClick = { if (!isLoading) showPhotoViewer = true }) {
                        if (isLoading) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        } else if (entry != null) {
                            val hazeState = rememberHazeState()

                            Box(
                                Modifier
                                    .fillMaxSize()
                            ) {
                                if (editedImagePaths.isEmpty()) {
                                    Image(
                                        Tabler.Outline.PhotoX,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(128.dp),
                                        colorFilter = ColorFilter.tint(Colors.text.copy(alpha = 0.2f))
                                    )
                                } else {
                                    AsyncImage(
                                        model = editedImagePaths.first(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .hazeSource(hazeState),
                                        contentScale = ContentScale.Crop
                                    )

                                    val blurStyle = CupertinoMaterials.ultraThin(Color(0x66272726))
                                    val shape = CircleShape

                                    Box(
                                        Modifier
                                            .padding(8.dp)
                                            .align(Alignment.BottomStart)
                                            .clip(shape)
                                            .hazeEffect(hazeState) {
                                                blurEffect {
                                                    style = blurStyle
                                                }
                                            }
                                            .padding(8.dp, 4.dp)
                                    ) {
                                        Text(
                                            editedImagePaths.size.toString(),
                                            color = Color(0xFFFFFFFF),
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Card(9.54f, onClick = { if (!isLoading) showDetailsModal = true }) {
                        if (isLoading) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        } else if (entry != null) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Tabler.Outline.Calendar,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Colors.text.copy(alpha = 0.5f)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    val localDateTime = entry!!.date
                                    val dateString =
                                        "${localDateTime.dayOfMonth} ${
                                            localDateTime.month.name.lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        } ${localDateTime.year}"
                                    Text(
                                        dateString,
                                        style = TextStyle(
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Colors.text.copy(alpha = 0.5f)
                                        )
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    editedDescription.ifBlank { "Add a description..." },
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = if (editedDescription.isBlank()) Colors.text.copy(alpha = 0.5f) else Colors.text,
                                        lineHeight = 22.sp
                                    ),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Icon(
                                        Tabler.Outline.MapPin,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = Colors.text.copy(alpha = 0.4f)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        if (!entry!!.address.isNullOrBlank()) entry!!.address!! else "${entry!!.latitude}, ${entry!!.longitude}",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            color = Colors.text.copy(alpha = 0.4f)
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

			// Bottom Save/Cancel buttons
			val hasChanges = remember(editedTitle, editedDescription, editedImagePaths, entry) {
				entry != null && (
						editedTitle != entry?.title ||
								editedDescription != entry?.description ||
								editedImagePaths != entry?.imagePaths
						)
			}

            val isFormValid = editedTitle.isNotBlank() && (entry?.latitude ?: latitude) != null && (entry?.longitude
                ?: longitude) != null

			if (hasChanges || entryId == null) {
				Row(
					Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .navigationBarsPadding(),
					horizontalArrangement = Arrangement.spacedBy(16.dp)
				) {
					Button(
						onClick = {
							if (entryId == null) {
								navigator.pop()
							} else {
								editedTitle = entry?.title ?: ""
								editedDescription = entry?.description ?: ""
								editedImagePaths = entry?.imagePaths ?: emptyList()
								isEditingTitle = false
								isEditingDescription = false
							}
						},
						colors = ButtonDefaults.buttonColors(containerColor = Colors.backgroundSecondary),
						border = BorderStroke(1.dp, Colors.borderColor),
						shape = RoundedCornerShape(12.dp)
					) {
						Text("Cancel", color = Colors.text)
					}

					Button(
                        enabled = isFormValid,
						onClick = {
							scope.launch {
								var synced = false
								if (isLoggedIn) {
									val response = if (entryId == null) {
										locationApiService.createLocation(
											title = editedTitle,
											description = editedDescription,
											latitude = latitude ?: 0.0,
                                            longitude = longitude ?: 0.0,
                                            creationDate = kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                                                .toLocalDateTime(TimeZone.currentSystemDefault())
										)
									} else {
										null
									}
									synced = response != null
								}

								val updatedEntry = JournalEntry(
									id = entryId ?: 0L,
									title = editedTitle,
									description = editedDescription,
									latitude = entry?.latitude ?: latitude ?: 0.0,
									longitude = entry?.longitude ?: longitude ?: 0.0,
									address = entry?.address ?: currentLocation?.address?.formattedAddress
									?: currentLocation?.name,
                                    date = entry?.date
                                        ?: kotlinx.datetime.Instant.fromEpochMilliseconds(System.currentTimeMillis())
                                            .toLocalDateTime(TimeZone.currentSystemDefault()),
									imagePaths = editedImagePaths,
									isSynced = synced
								)

								if (entryId == null) {
									repository.insert(updatedEntry)
								} else {
									repository.update(updatedEntry)
								}

								if (isLoggedIn && !synced) {
                                    SyncUtils.triggerSync(context)
								}

								if (entryId == null) {
									locationService.clearSelection()
									navigator.pop()
								} else {
									entry = updatedEntry
									isEditingTitle = false
									isEditingDescription = false
								}
							}
						},
						colors = ButtonDefaults.buttonColors(containerColor = Colors.text),
						shape = RoundedCornerShape(12.dp)
					) {
						Text("Save", color = Colors.background)
					}
				}
			}

			if (showDeleteEntryConfirmation) {
				AlertDialog(
					onDismissRequest = { showDeleteEntryConfirmation = false },
					title = { Text("Delete Entry") },
					text = { Text("Are you sure you want to delete this entry? This action cannot be undone.") },
					confirmButton = {
						TextButton(
							onClick = {
								scope.launch {
									entry?.let { repository.delete(it) }
									navigator.pop()
								}
							},
							colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
						) {
							Text("Delete")
						}
					},
					dismissButton = {
						TextButton(onClick = { showDeleteEntryConfirmation = false }) {
							Text("Cancel")
						}
					}
				)
			}

			if (showPhotoViewer && entry != null) {
				val pagerState = rememberPagerState { editedImagePaths.size }
				Dialog(
					onDismissRequest = { showPhotoViewer = false },
					properties = DialogProperties(usePlatformDefaultWidth = false)
				) {
					Box(
						Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f))
					) {
						if (editedImagePaths.isEmpty()) {
							Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
								Column(horizontalAlignment = Alignment.CenterHorizontally) {
									Icon(
										Tabler.Outline.PhotoOff,
										contentDescription = null,
										tint = Color.White.copy(alpha = 0.5f),
										modifier = Modifier.size(64.dp)
									)
									Spacer(Modifier.height(16.dp))
									Text("No photos yet", color = Color.White.copy(alpha = 0.5f))
								}
							}
						} else {
							HorizontalPager(
								state = pagerState,
								modifier = Modifier.fillMaxSize()
							) { index ->
								AsyncImage(
									model = editedImagePaths[index],
									contentDescription = null,
									modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(16.dp)),
									contentScale = ContentScale.Fit
								)
							}
						}

						// Close button
						Surface(
							onClick = { showPhotoViewer = false },
							shape = CircleShape,
							color = Color.White.copy(alpha = 0.2f),
							modifier = Modifier
                                .padding(16.dp)
                                .statusBarsPadding()
                                .size(40.dp)
                                .align(Alignment.TopStart)
						) {
							Box(contentAlignment = Alignment.Center) {
								Icon(
									Tabler.Outline.X,
									contentDescription = "Close",
									tint = Color.White,
									modifier = Modifier.size(20.dp)
								)
							}
						}

						// Add/Delete buttons at the bottom
						Row(
							Modifier
                                .align(Alignment.BottomCenter)
                                .padding(32.dp)
                                .navigationBarsPadding(),
							horizontalArrangement = Arrangement.spacedBy(16.dp)
						) {
							Button(
								onClick = {
									photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
								},
								colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
								shape = RoundedCornerShape(12.dp)
							) {
								Icon(Tabler.Outline.Plus, contentDescription = "Add", tint = Color.White)
								Spacer(Modifier.width(8.dp))
								Text("Add", color = Color.White)
							}

							if (editedImagePaths.isNotEmpty()) {
								Button(
									onClick = { showDeletePhotoConfirmation = true },
									colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.6f)),
									shape = RoundedCornerShape(12.dp)
								) {
									Icon(Tabler.Outline.Trash, contentDescription = "Delete", tint = Color.White)
									Spacer(Modifier.width(8.dp))
									Text("Delete", color = Color.White)
								}
							}
						}
					}
				}

				if (showDeletePhotoConfirmation) {
					AlertDialog(
						onDismissRequest = { showDeletePhotoConfirmation = false },
						title = { Text("Delete Photo") },
						text = { Text("Are you sure you want to delete this photo?") },
						confirmButton = {
							TextButton(
								onClick = {
									scope.launch {
										val updatedImagePaths = editedImagePaths.toMutableList()
										updatedImagePaths.removeAt(pagerState.currentPage)
										editedImagePaths = updatedImagePaths
										showDeletePhotoConfirmation = false
									}
								},
								colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
							) {
								Text("Delete")
							}
						},
						dismissButton = {
							TextButton(onClick = { showDeletePhotoConfirmation = false }) {
								Text("Cancel")
							}
						}
					)
				}
			}

			if (showDetailsModal && entry != null) {
				Dialog(onDismissRequest = { showDetailsModal = false }) {
					Surface(
						modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
						shape = RoundedCornerShape(24.dp),
						color = Colors.backgroundSecondary,
						border = BorderStroke(2.dp, Colors.borderColor)
					) {
						Column(
							Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							if (isEditingTitle) {
								BasicTextField(
									value = editedTitle,
									onValueChange = { editedTitle = it },
									textStyle = TextStyle(
										fontSize = 24.sp,
										fontWeight = FontWeight.Bold,
										textAlign = TextAlign.Center,
										color = Colors.text
									),
									modifier = Modifier
                                        .focusRequester(modalTitleFocusRequester)
                                        .fillMaxWidth(),
									keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
									keyboardActions = KeyboardActions(onDone = {
										isEditingTitle = false
										focusManager.clearFocus()
									}),
									singleLine = true
								)
								LaunchedEffect(Unit) {
									modalTitleFocusRequester.requestFocus()
								}
							} else {
								Text(
									editedTitle.ifBlank { if (entryId == null) "New Entry" else "No Title" },
									style = TextStyle(
										fontSize = 24.sp,
										fontWeight = FontWeight.Bold,
										color = if (editedTitle.isBlank()) Colors.text.copy(alpha = 0.5f) else Colors.text
									),
									textAlign = TextAlign.Center,
									modifier = Modifier.clickable { isEditingTitle = true }
								)
							}
							Spacer(Modifier.height(16.dp))
							if (isEditingDescription) {
								BasicTextField(
									value = editedDescription,
									onValueChange = { editedDescription = it },
									textStyle = TextStyle(
										fontSize = 16.sp,
										color = Colors.text,
										textAlign = TextAlign.Center
									),
									modifier = Modifier
                                        .focusRequester(descriptionFocusRequester)
                                        .fillMaxWidth(),
									keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
									keyboardActions = KeyboardActions(onDone = {
										isEditingDescription = false
										focusManager.clearFocus()
									})
								)
								LaunchedEffect(Unit) {
									descriptionFocusRequester.requestFocus()
								}
							} else {
								Text(
									editedDescription.ifBlank { "Add a description..." },
									style = TextStyle(
										fontSize = 16.sp,
										color = if (editedDescription.isBlank()) Colors.text.copy(alpha = 0.5f) else Colors.text
									),
									textAlign = TextAlign.Center,
									modifier = Modifier.clickable { isEditingDescription = true }
								)
							}
							Spacer(Modifier.height(16.dp))
							HorizontalDivider(color = Colors.borderColor.copy(alpha = 0.5f))
							Spacer(Modifier.height(16.dp))
							Text(
								if (!entry!!.address.isNullOrBlank()) entry!!.address!! else "${entry!!.latitude}, ${entry!!.longitude}",
								style = TextStyle(
									fontSize = 14.sp,
									color = Colors.text.copy(alpha = 0.6f)
								),
								textAlign = TextAlign.Center
							)
						}
					}
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

    @Composable
    private fun Card(rotation: Float, onClick: () -> Unit = {}, content: @Composable () -> Unit) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .size(240.dp)
                .rotate(rotation),
            shape = RoundedCornerShape(24.dp),
            color = Colors.backgroundSecondary,
            border = BorderStroke(4.dp, Colors.borderColor),
            shadowElevation = 8.dp
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                content()
            }
        }
    }
}