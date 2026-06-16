package dev.pandasystems.logmypos_client.services.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AuthService {
    val isLoggedIn: StateFlow<Boolean>
    suspend fun login(username: String, password: String): Boolean
    fun logout()
}

class AuthServiceImpl : AuthService {
    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    override suspend fun login(username: String, password: String): Boolean {
        // Simulate API call delay
        delay(1500)
        
        // Hardcoded user for simulation
        return if (username == "admin" && password == "password123") {
            _isLoggedIn.value = true
            true
        } else {
            false
        }
    }

    override fun logout() {
        _isLoggedIn.value = false
    }
}
