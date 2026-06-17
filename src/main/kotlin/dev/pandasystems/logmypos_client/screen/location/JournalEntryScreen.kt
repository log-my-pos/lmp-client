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
import androidx.compose.ui.platform.LocalInspectionMode
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
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.blur.materials.CupertinoMaterials
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.pandasystems.logmypos_client.data.JournalEntry
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
private fun PreviewBaseJournalScreen() = SetupPreviewScreen(JournalEntryScreen(0L))

class JournalEntryScreen(
	val entryId: Long
) : Screen {
	@Composable
	override fun Content() {
		val isPreview = LocalInspectionMode.current

		val repository = koinInject<JournalRepository>()
		var entry by remember { mutableStateOf<JournalEntry?>(null) }
		var isLoading by remember { mutableStateOf(true) }

		var showPhotoViewer by remember { mutableStateOf(false) }
		var showDetailsModal by remember { mutableStateOf(false) }

		var isEditingTitle by remember { mutableStateOf(false) }
		var editedTitle by remember { mutableStateOf("") }
		var showDeleteEntryConfirmation by remember { mutableStateOf(false) }
		var showDeletePhotoConfirmation by remember { mutableStateOf(false) }

		val navigator = LocalNavigator.currentOrThrow
		val scope = rememberCoroutineScope()
		val context = LocalContext.current
		val focusManager = LocalFocusManager.current
		val titleFocusRequester = remember { FocusRequester() }

		val photoPickerLauncher = rememberLauncherForActivityResult(
			contract = ActivityResultContracts.PickMultipleVisualMedia(),
			onResult = { uris ->
				scope.launch {
					entry?.let { currentEntry ->
						val newImagePaths = uris.mapNotNull { uri ->
							saveImageToInternalStorage(context, uri)
						}
						val updatedEntry = currentEntry.copy(imagePaths = currentEntry.imagePaths + newImagePaths)
						repository.update(updatedEntry)
						entry = updatedEntry
					}
				}
			}
		)

		LaunchedEffect(entryId) {
			entry = repository.getEntryById(entryId)
			if (entry != null) {
				editedTitle = entry!!.title
				isLoading = false
			}
		}

		Box(Modifier.fillMaxSize()) {
			Column(
				Modifier.fillMaxSize(),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.SpaceEvenly
			) {
				Spacer(Modifier.height(56.dp)) // For Top Bar
				Card(-11f, onClick = { if (!isLoading) showPhotoViewer = true }) {
				if (isLoading) {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
				} else if (entry != null) {
					val hazeState = rememberHazeState()

					Box(
						Modifier
							.fillMaxSize()
					) {
						if (entry!!.imagePaths.isEmpty()) {
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
								model = entry!!.imagePaths.first(),
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
									entry!!.imagePaths.size.toString(),
									color = Color(0xFFFFFFFF),
								)
							}
						}
					}
				}
			}
				Card(9.54f, onClick = { if (!isLoading) showDetailsModal = true }) {
				if (isLoading) {
					Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
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
							val localDateTime = Instant.fromEpochMilliseconds(entry!!.date)
								.toLocalDateTime(TimeZone.currentSystemDefault())
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
							entry!!.description,
							style = TextStyle(
								fontSize = 16.sp,
								color = Colors.text,
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

			// Top Bar
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
								scope.launch {
									entry?.let {
										val updatedEntry = it.copy(title = editedTitle)
										repository.update(updatedEntry)
										entry = updatedEntry
									}
								}
								focusManager.clearFocus()
							}),
							singleLine = true
						)
						LaunchedEffect(Unit) {
							titleFocusRequester.requestFocus()
						}
				} else {
						Text(
							entry?.title ?: "",
							style = TextStyle(
								fontSize = 18.sp,
								fontWeight = FontWeight.Bold,
								color = Colors.text
							),
							modifier = Modifier.clickable { isEditingTitle = true },
							maxLines = 1,
							overflow = TextOverflow.Ellipsis
						)
					}
				}

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
				val pagerState = rememberPagerState { entry!!.imagePaths.size }
				Dialog(
					onDismissRequest = { showPhotoViewer = false },
					properties = DialogProperties(usePlatformDefaultWidth = false)
				) {
					Box(
						Modifier
							.fillMaxSize()
							.background(Color.Black.copy(alpha = 0.9f))
					) {
						if (entry!!.imagePaths.isEmpty()) {
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
									model = entry!!.imagePaths[index],
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

							if (entry!!.imagePaths.isNotEmpty()) {
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
										val updatedImagePaths = entry!!.imagePaths.toMutableList()
										updatedImagePaths.removeAt(pagerState.currentPage)
										val updatedEntry = entry!!.copy(imagePaths = updatedImagePaths)
										repository.update(updatedEntry)
										entry = updatedEntry
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
							Text(
								entry!!.title,
								style = TextStyle(
									fontSize = 24.sp,
									fontWeight = FontWeight.Bold,
									color = Colors.text
								)
							)
							Spacer(Modifier.height(16.dp))
							Text(
								entry!!.description,
								style = TextStyle(
									fontSize = 16.sp,
									color = Colors.text
								),
								textAlign = TextAlign.Center
							)
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

		} // End of outer Box
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