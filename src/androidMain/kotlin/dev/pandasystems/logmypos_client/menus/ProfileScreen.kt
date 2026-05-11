package dev.pandasystems.logmypos_client.menus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.*
import dev.pandasystems.logmypos_client.navigation.localNavController
import java.util.UUID

data class Profile(
	val profileId: UUID
)

@Preview
@Composable
fun ProfileScreen() {
	MaterialTheme {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
		) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.systemBarsPadding()
			) {
				// Header with back button
				item {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.height(56.dp),
						contentAlignment = Alignment.CenterStart
					) {
						IconButton(
							onClick = { localNavController.navigateBack() },
							modifier = Modifier.align(Alignment.CenterStart)
						) {
							Icon(Tabler.Outline.ArrowLeft, contentDescription = "Back")
						}
						Text(
							"Profile",
							modifier = Modifier
								.align(Alignment.Center),
							fontSize = 20.sp,
							fontWeight = FontWeight.Bold
						)
					}
					HorizontalDivider()
				}

				// Profile Card Section
				item {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp)
							.background(
								MaterialTheme.colorScheme.surfaceContainer,
								RoundedCornerShape(12.dp)
							)
							.padding(24.dp)
					) {
						Column(
							modifier = Modifier.fillMaxWidth(),
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							// Avatar
							Box(
								modifier = Modifier
									.size(80.dp)
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.primary),
								contentAlignment = Alignment.Center
							) {
								Icon(
									Tabler.Outline.User,
									contentDescription = "Profile Avatar",
									modifier = Modifier.size(48.dp),
									tint = MaterialTheme.colorScheme.onPrimary
								)
							}

							Spacer(modifier = Modifier.height(16.dp))

							// User Name
							Text(
								"John Doe",
								fontSize = 20.sp,
								fontWeight = FontWeight.Bold,
								color = MaterialTheme.colorScheme.onSurface
							)

							Spacer(modifier = Modifier.height(4.dp))

							// User Email
							Text(
								"john.doe@example.com",
								fontSize = 14.sp,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)

							Spacer(modifier = Modifier.height(12.dp))

							// User Status Badge
							Box(
								modifier = Modifier
									.background(
										MaterialTheme.colorScheme.primaryContainer,
										RoundedCornerShape(20.dp)
									)
									.padding(horizontal = 12.dp, vertical = 6.dp)
							) {
								Text(
									"Active",
									fontSize = 12.sp,
									color = MaterialTheme.colorScheme.onPrimaryContainer,
									fontWeight = FontWeight.SemiBold
								)
							}
						}
					}
				}

				// Menu Items
				item {
					Spacer(modifier = Modifier.height(8.dp))
				}

				items(profileMenuItems.size) { index ->
					val item = profileMenuItems[index]
					ProfileMenuItem(
						icon = item.icon,
						title = item.title,
						onClick = item.onClick
					)
					if (index < profileMenuItems.size - 1) {
						HorizontalDivider(
							modifier = Modifier.padding(horizontal = 16.dp),
							thickness = 0.5.dp
						)
					}
				}

				// Logout Button
				item {
					Spacer(modifier = Modifier.height(8.dp))
					Button(
						onClick = { /* Handle logout */ },
						modifier = Modifier
							.fillMaxWidth()
							.padding(16.dp)
							.height(48.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.errorContainer,
							contentColor = MaterialTheme.colorScheme.onErrorContainer
						),
						shape = RoundedCornerShape(8.dp)
					) {
						Icon(
							Tabler.Outline.Logout,
							contentDescription = null,
							modifier = Modifier
								.size(20.dp)
								.padding(end = 8.dp)
						)
						Text("Logout", fontWeight = FontWeight.Medium)
					}
					Spacer(modifier = Modifier.height(24.dp))
				}
			}
		}
	}
}


@Composable
private fun ProfileMenuItem(
	icon: @Composable () -> Unit,
	title: String,
	onClick: () -> Unit
) {
	Surface(
		onClick = onClick,
		modifier = Modifier.fillMaxWidth(),
		color = MaterialTheme.colorScheme.surface
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Box(
				modifier = Modifier
					.size(40.dp)
					.background(
						MaterialTheme.colorScheme.primaryContainer,
						RoundedCornerShape(8.dp)
					),
				contentAlignment = Alignment.Center
			) {
				icon()
			}

			Spacer(modifier = Modifier.width(16.dp))

			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					title,
					fontSize = 16.sp,
					fontWeight = FontWeight.Medium,
					color = MaterialTheme.colorScheme.onSurface
				)
			}

			Icon(
				Tabler.Outline.ChevronRight,
				contentDescription = null,
				modifier = Modifier.size(20.dp),
				tint = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

// Data class for menu items
data class ProfileMenuItemData(
	val icon: @Composable () -> Unit,
	val title: String,
	val onClick: () -> Unit
)

// Menu items list
val profileMenuItems = listOf(
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.User, contentDescription = null) },
		title = "Edit Profile",
		onClick = { /* Handle edit profile */ }
	),
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.Lock, contentDescription = null) },
		title = "Change Password",
		onClick = { /* Handle change password */ }
	),
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.Bell, contentDescription = null) },
		title = "Notifications",
		onClick = { /* Handle notifications */ }
	),
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.Settings, contentDescription = null) },
		title = "Settings",
		onClick = { /* Handle settings */ }
	),
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.HelpCircle, contentDescription = null) },
		title = "Help & Support",
		onClick = { /* Handle help */ }
	),
	ProfileMenuItemData(
		icon = { Icon(Tabler.Outline.FileText, contentDescription = null) },
		title = "About",
		onClick = { /* Handle about */ }
	)
)