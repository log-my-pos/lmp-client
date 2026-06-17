package dev.pandasystems.logmypos_client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.MapPin
import com.composables.icons.tabler.outline.Plus
import com.composables.icons.tabler.outline.X
import com.mapbox.maps.dsl.cameraOptions
import dev.pandasystems.logmypos_client.models.GlobalData
import dev.pandasystems.logmypos_client.screen.location.AddLocationScreen
import dev.pandasystems.logmypos_client.services.location.LocationService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreview
import dev.pandasystems.logmypos_client.utils.backTo
import dev.pandasystems.logmypos_client.utils.getNavigator
import org.koin.compose.koinInject

@Preview
@Composable
private fun LocationViewOverlayPreview() = SetupPreview {
    LocationViewOverlay()
}

@Composable
fun LocationViewOverlay() {
    val navigator = getNavigator()
    val locationService = koinInject<LocationService>()
    val globalData = koinInject<GlobalData>()
    val mapViewportState = globalData.mapViewportState

    val location = locationService.selectedLocation ?: return

    LaunchedEffect(location) {
        mapViewportState.flyTo(cameraOptions {
            center(location.coordinate)
            zoom(14.0)
        })
    }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .dropShadow(
                RoundedCornerShape(24.dp),
                Shadow(
                    radius = 16.dp,
                    color = Colors.shadow
                )
            ),
        shape = RoundedCornerShape(24.dp),
        color = Colors.background
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F5F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Tabler.Outline.MapPin,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Colors.text
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Selected Location",
                        fontSize = 12.sp,
                        color = Colors.text.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = location.address?.formattedAddress ?: location.name,
                        fontSize = 16.sp,
                        color = Colors.text,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                IconButton(
                    onClick = {
                        navigator.backTo<MainScreen>()
                        locationService.clearSelection()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Tabler.Outline.X,
                        contentDescription = "Clear selection",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Button(
                onClick = {
                    val coordinate = location.coordinate
                    navigator.navigateTo(AddLocationScreen(coordinate.latitude(), coordinate.longitude()))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(12.dp)
            ) {
                Icon(
                    imageVector = Tabler.Outline.Plus,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add new entry",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}