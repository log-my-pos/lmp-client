package dev.pandasystems.logmypos_client.services.auth

import dev.pandasystems.logmypos_client.api.LogMyPosApi
import dev.pandasystems.logmypos_client.api.models.AuthResponse
import dev.pandasystems.logmypos_client.api.models.LoginRequest
import dev.pandasystems.logmypos_client.api.models.RegisterRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthServiceImpl(
    private val api: LogMyPosApi,
    private val tokenManager: TokenManager
) : AuthService {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        tokenManager.token.onEach {
            _isLoggedIn.value = it != null
        }.launchIn(scope)
    }

    override suspend fun login(username: String, password: String): Boolean {
        return try {
            val response = api.client.post("/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(identifier = username, password = password))
            }

            if (response.status == HttpStatusCode.OK) {
                val authResponse = response.body<AuthResponse>()
                tokenManager.saveToken(authResponse.token)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun signup(username: String, email: String, password: String): Boolean {
        return try {
            val response = api.client.post("/api/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username = username, email = email, password = password))
            }

            if (response.status == HttpStatusCode.Created) {
                val authResponse = response.body<AuthResponse>()
                tokenManager.saveToken(authResponse.token)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun logout() {
        scope.launch {
            tokenManager.clearToken()
        }
    }
}
