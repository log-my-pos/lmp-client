package dev.pandasystems.logmypos_client.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.pandasystems.logmypos_client.components.InputField
import dev.pandasystems.logmypos_client.screen.main.MainScreen
import dev.pandasystems.logmypos_client.services.auth.AuthService
import dev.pandasystems.logmypos_client.theme.Colors
import dev.pandasystems.logmypos_client.theme.Colors.backgroundSecondary
import dev.pandasystems.logmypos_client.utils.SetupPreviewScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Preview
@Composable
private fun PreviewSignupScreen() = SetupPreviewScreen(SignupScreen())

class SignupScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val authService = koinInject<AuthService>()
        val scope = rememberCoroutineScope()

        val usernameState = rememberTextFieldState()
        val emailState = rememberTextFieldState()
        val passwordState = rememberTextFieldState()
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.background)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Join LogMyPos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Colors.text
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create an account to keep your memories safe in the cloud.",
                fontSize = 16.sp,
                color = Colors.text.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Username",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.text
                )
                InputField(
                    state = usernameState,
                    placeholder = "Pick a username",
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = backgroundSecondary,
                    enabled = !isLoading
                )

                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.text
                )
                InputField(
                    state = emailState,
                    placeholder = "Enter your email",
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = backgroundSecondary,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !isLoading
                )

                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.text
                )
                InputField(
                    state = passwordState,
                    placeholder = "Create a password",
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = backgroundSecondary,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = !isLoading
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    val username = usernameState.text.toString()
                    val email = emailState.text.toString()
                    val password = passwordState.text.toString()

                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill in all fields"
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        val success = authService.signup(username, email, password)
                        isLoading = false
                        if (success) {
                            navigator.replaceAll(MainScreen())
                        } else {
                            errorMessage = "Signup failed. Please try again."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Colors.text,
                    contentColor = Colors.background
                ),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Colors.background,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    navigator.pop()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Row {
                    Text(
                        "Already have an account? ",
                        color = Colors.text.copy(alpha = 0.5f),
                        fontSize = 16.sp
                    )
                    Text(
                        "Login",
                        color = Colors.text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
