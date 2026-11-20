package gcd.testing.firstproject.biometrics.data.network

import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.model.UnauthorizedException
import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createBiometricHttpClient(
    sessionRepositoryProvider: () -> BiometricSessionRepository,
): HttpClient {
    val bearerInterceptorPlugin = createClientPlugin("BiometricSessionBearerPlugin") {
        onRequest { request, _ ->
            val path = request.url.encodedPath
            val isPublicAuthEndpoint = path.contains("/auth/biometric/") || path.contains("/auth/login")
            if (!isPublicAuthEndpoint) {
                val state = runCatching { sessionRepositoryProvider().sessionState.value }.getOrNull()
                if (state is SessionState.Unlocked && state.accessToken.isNotBlank()) {
                    request.headers.remove(HttpHeaders.Authorization)
                    request.headers.append(HttpHeaders.Authorization, "Bearer ${state.accessToken}")
                }
            }
        }
    }

    return HttpClient {
        expectSuccess = true

        install(DefaultRequest) {
            header("ngrok-skip-browser-warning", "true")
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        // NOTA: No se usa el plugin Auth con refreshTokens { ... }.
        // El refresco ocurre exclusivamente dentro de unlockAndAuthenticate().
        install(bearerInterceptorPlugin)

        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, request ->
                val responseException = cause as? ResponseException ?: return@handleResponseExceptionWithRequest
                val response = responseException.response
                val status = response.status
                val path = request.url.encodedPath
                val isLoginEndpoint = path.contains("/auth/login")
                val isBiometricAuthEndpoint = path.contains("/auth/biometric/")

                if (status == HttpStatusCode.Unauthorized && isLoginEndpoint) {
                    throw UnauthorizedException(
                        message = "Credenciales inválidas (401 Unauthorized).",
                        statusCode = HttpStatusCode.Unauthorized.value,
                    )
                }

                if (status == HttpStatusCode.Unauthorized && !isBiometricAuthEndpoint) {
                    runCatching { sessionRepositoryProvider().lockSession() }
                    throw UnauthorizedException(
                        message = "401 Unauthorized en endpoint de negocio ($path). Sesión bloqueada automáticamente.",
                        statusCode = HttpStatusCode.Unauthorized.value,
                    )
                }

                if (
                    isBiometricAuthEndpoint &&
                    (status == HttpStatusCode.Unauthorized || status == HttpStatusCode.Forbidden)
                ) {
                    throw UnauthorizedException(
                        message = "Sesión revocada o expirada, inicia sesión nuevamente",
                        statusCode = status.value,
                    )
                }

                val errorBody = runCatching { response.bodyAsText() }.getOrNull().orEmpty().trim()
                val detail = if (errorBody.isNotBlank()) ": $errorBody" else ""
                throw IllegalStateException(
                    "Error HTTP ${status.value} (${status.description}) en $path$detail",
                    responseException,
                )
            }
        }
    }
}
