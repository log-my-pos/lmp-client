package dev.pandasystems.logmypos_client.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.User

@Composable
@Preview
fun Avatar(
	size: Dp = 80.dp
) {
	Box(
		modifier = Modifier
			.size(size)
			.clip(CircleShape)
			.background(MaterialTheme.colorScheme.primary),
		contentAlignment = Alignment.Center
	) {
		if (false /* TODO: If profile image is available */) {
			// TODO: Show the profile image instead of user icon
			Icon(
				Tabler.Outline.User,
				contentDescription = "Profile Avatar",
				modifier = Modifier.size(size / 1.5f),
				tint = MaterialTheme.colorScheme.onPrimary
			)
		} else {
			Icon(
				Tabler.Outline.User,
				contentDescription = "Profile Avatar",
				modifier = Modifier.size(size / 1.5f),
				tint = MaterialTheme.colorScheme.onPrimary
			)
		}
	}
}