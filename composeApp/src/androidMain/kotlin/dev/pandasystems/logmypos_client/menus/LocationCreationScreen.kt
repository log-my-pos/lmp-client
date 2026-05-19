package dev.pandasystems.logmypos_client.menus

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
data class LocationCreationRoute(
	val imageUris: List<String>,
)

@Composable
@Preview
fun LocationCreationScreen(
	navController: NavHostController? = null,
	imageUris: List<URI> = emptyList(),
) {
	
}