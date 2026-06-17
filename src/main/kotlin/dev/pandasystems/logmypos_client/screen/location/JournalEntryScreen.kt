package dev.pandasystems.logmypos_client.screen.location

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.blur.blurEffect
import dev.chrisbanes.haze.blur.materials.CupertinoMaterials
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.pandasystems.logmypos_client.R
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen

@Preview
@Composable
private fun PreviewBaseJournalScreen() = SetupPreviewScreen(JournalEntryScreen())

class JournalEntryScreen : Screen {
    @Composable
    override fun Content() {
        val isPreview = LocalInspectionMode.current

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(-11f) {
                val hazeState = rememberHazeState()

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = if (isPreview) {
                            R.drawable.preview_image2
                        } else {
                            "https://picsum.photos/300/300"
                        },
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
                            "20",
                            color = Color(0xFFFFFFFF),
                        )
                    }
                }
            }
            Card(9.54f) { }
        }
    }

    @Composable
    private fun Card(rotation: Float, content: @Composable () -> Unit) {
        Surface(
            Modifier
                .size(240.dp)
                .rotate(rotation),
            shape = RoundedCornerShape(24.dp),
            color = Colors.backgroundSecondary,
            border = BorderStroke(4.dp, Colors.borderColor),
            shadowElevation = 8.dp
        ) {
            Box(Modifier
                .fillMaxSize()
                .padding(4.dp)) {
                content()
            }
        }
    }
}