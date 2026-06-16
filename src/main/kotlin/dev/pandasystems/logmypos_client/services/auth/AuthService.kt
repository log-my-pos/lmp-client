package dev.pandasystems.logmypos_client.services.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthService {
    val isLoggedIn: StateFlow<Boolean>

    suspend fun login(username: String, password: String): Boolean
    suspend fun signup(username: String, email: String, password: String): Boolean
    fun logout()
}