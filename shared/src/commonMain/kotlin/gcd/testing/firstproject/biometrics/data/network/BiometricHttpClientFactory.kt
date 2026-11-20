package gcd.testing.firstproject.biometrics.data.network

import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.model.UnauthorizedException
import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.request
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
            val isBiometricAuthEndpoint = path.contains("/auth/biometric/")
            if (!isBiometricAuthEndpoint) {
                val state = runCatching { sessionRepositoryProvider().sessionState.value }.getOrNull()
                if (state is SessionState.Unlocked && state.accessToken.isNotBlank()) {
                    request.headers.remove(HttpHeaders.Authorization)
                    request.headers.append(HttpHeaders.Authorization, "Bearer ${state.accessToken}")
                }
            }
        }
    }

    return HttpClient {
        expectSuccess = false

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
            validateResponse { response ->
                val path = response.request.url.encodedPath
                val isBiometricAuthEndpoint = path.contains("/auth/biometric/")

                if (response.status == HttpStatusCode.Unauthorized && !isBiometricAuthEndpoint) {
                    runCatching { sessionRepositoryProvider().lockSession() }
                    throw UnauthorizedException(
                        message = "401 Unauthorized en endpoint de negocio ($path). Sesión bloqueada automáticamente.",
                        statusCode = HttpStatusCode.Unauthorized.value,
                    )
                }

                if (
                    isBiometricAuthEndpoint &&
                    (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden)
                ) {
                    throw UnauthorizedException(
                        message = "Sesión revocada o expirada, inicia sesión nuevamente",
                        statusCode = response.status.value,
                    )
                }
            }
        }
    }
}
