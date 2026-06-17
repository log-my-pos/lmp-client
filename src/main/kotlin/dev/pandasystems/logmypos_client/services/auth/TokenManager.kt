package dev.pandasystems.logmypos_client.services.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface TokenManager {
    val token: Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}

class FakeTokenManager : TokenManager {
    override val token: Flow<String?> = flowOf(null)
    override suspend fun saveToken(token: String) {}
    override suspend fun clearToken() {}
}