package dev.pandasystems.logmypos_client.api

import dev.pandasystems.logmypos_client.services.auth.TokenManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

class LogMyPosApi(private val tokenManager: TokenManager) {
    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }

        install(Auth) {
            bearer {
                loadTokens {
                    tokenManager.token.firstOrNull()?.let {
                        BearerTokens(it, "")
                    }
                }
            }
        }

        defaultRequest {
            url("https://logmypos-backend.coolify.pandasystems.dev")
        }
    }
}
