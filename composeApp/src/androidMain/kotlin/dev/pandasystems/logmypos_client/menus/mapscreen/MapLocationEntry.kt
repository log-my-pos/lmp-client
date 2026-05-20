package dev.pandasystems.logmypos_client.menus.mapscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.pandasystems.logmypos_client.theme.borderLightColor
import dev.pandasystems.logmypos_client.theme.interactableBackgroundLightColor
import dev.pandasystems.logmypos_client.theme.shadowColor
import kotlinx.serialization.Serializable

@Serializable
data object MapLocationEntryOverlayRoute

@Composable
@Preview
fun MapLocationEntryOverlay(
	mapNavController: NavHostController? = null
) {
	Box(
		modifier = Modifier
			.fillMaxSize()
	) {
		Column(
			modifier = Modifier
				.fillMaxSize(),
			verticalArrangement = Arrangement.SpaceEvenly,
			horizontalAlignment = Alignment.CenterHorizontally,
		) {
			// Image card
			Card(-11f, (-40).dp) {}

			// General details, but previews the user defined description about the location
			Card(9.54f, 40.dp) {}
		}
	}
}

@Composable
private fun Card(
	rotation: Float,
	xOffset: Dp,
	content: @Composable () -> Unit
) {
	val roundedShape = RoundedCornerShape(24.dp)
	Box(
		modifier = Modifier
			.absoluteOffset(x = xOffset)
	) {
		Surface(
			modifier = Modifier
				.rotate(rotation)
				.size(240.dp)
				.dropShadow(roundedShape, Shadow(12.dp, shadowColor)),
			shape = roundedShape,
			color = interactableBackgroundLightColor,
			border = BorderStroke(4.dp, borderLightColor),
		) {
			content()
		}
	}
}