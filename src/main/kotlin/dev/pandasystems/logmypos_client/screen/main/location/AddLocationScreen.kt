package dev.pandasystems.logmypos_client.screen.main.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.composables.icons.tabler.Tabler
import com.composables.icons.tabler.outline.ArrowLeft
import com.composables.icons.tabler.outline.Camera
import com.composables.icons.tabler.outline.MapPin
import dev.pandasystems.logmypos_client.components.InputField
import kotlinx.serialization.Serializable

@Preview
@Composable
private fun PreviewAddLocationScreen() {
    AddLocationScreen(
        navController = rememberNavController(),
        address = "123 Main St"
    )
}

@Serializable
data class AddLocationRoute(val address: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationScreen(
    navController: NavController,
    address: String,
    titleState: TextFieldState = rememberTextFieldState(),
    descriptionState: TextFieldState = rememberTextFieldState()
) {
    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Add Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Tabler.Outline.ArrowLeft, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Picker Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Tabler.Outline.Camera,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.Gray
                    )
                    Text("Add Photo", color = Color.Gray)
                }
            }

            Text(
                text = "Location Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Tabler.Outline.MapPin,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = address, fontSize = 16.sp)
            }

            Text(
                text = "Title",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            InputField(
                state = titleState,
                placeholder = "Give your memory a name",
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFF0F0F0)
            )

            Text(
                text = "Description",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            InputField(
                state = descriptionState,
                placeholder = "What happened here?",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                backgroundColor = Color(0xFFF0F0F0),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // TODO: Save the entry
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Save Entry", fontSize = 18.sp)
            }
        }
    }
}
