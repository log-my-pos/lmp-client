package dev.pandasystems.logmypos_client.api.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val role: String
)

@Serializable
data class AuthResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String
)

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class ErrorResponse(
    val error: String
)
