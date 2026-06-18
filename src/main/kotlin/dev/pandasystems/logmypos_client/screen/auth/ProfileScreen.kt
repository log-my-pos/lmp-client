package dev.pandasystems.logmypos_client.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.pandasystems.logmypos_client.repository.JournalRepository
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import dev.pandasystems.logmypos_client.utils.SyncUtils
import org.koin.compose.koinInject

@Preview
@Composable
private fun PreviewProfileScreen() = SetupPreviewScreen(ProfileScreen())

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = koinInject<AuthService>()
        val repository = koinInject<JournalRepository>()
        val context = LocalContext.current

        val unsyncedEntries by repository.unsyncedEntries.collectAsState(initial = emptyList())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Colors.text
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You are currently logged in.",
                fontSize = 16.sp,
                color = Colors.text
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sync Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Colors.backgroundSecondary,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sync Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Colors.text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (unsyncedEntries.isEmpty()) "All entries synced" else "${unsyncedEntries.size} entries pending sync",
                        fontSize = 14.sp,
                        color = Colors.text
                    )
                    if (unsyncedEntries.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { SyncUtils.triggerSync(context) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Colors.text,
                                contentColor = Colors.background
                            )
                        ) {
                            Text("Sync Now")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    authService.logout()
                    navigator.pop()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colors.text,
                    contentColor = Colors.background
                ),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text("Logout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navigator.pop() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Back",
                    color = Colors.text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

}
