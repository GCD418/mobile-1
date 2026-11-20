package gcd.testing.firstproject.biometrics.data.datasource

import gcd.testing.firstproject.biometrics.data.dto.InitialTokenRequest
import gcd.testing.firstproject.biometrics.data.dto.LoginRequest
import gcd.testing.firstproject.biometrics.data.dto.LoginResponse
import gcd.testing.firstproject.biometrics.data.dto.RefreshTokenRequest
import gcd.testing.firstproject.biometrics.data.dto.TokenPairResponse
import gcd.testing.firstproject.biometrics.domain.model.TokenPair
import gcd.testing.firstproject.biometrics.domain.model.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorBiometricRemoteDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL,
) : BiometricRemoteDataSource {

    override suspend fun fetchEnrollmentToken(
        email: String,
        password: String,
        deviceId: String,
        deviceName: String,
    ): String {
        val response = httpClient.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            if (deviceId.isNotBlank()) {
                header("X-Device-Id", deviceId)
            }
            setBody(
                LoginRequest(
                    email = email,
                    password = password,
                    deviceId = deviceId,
                )
            )
        }
        ensureSuccess(
            response = response,
            unauthorizedMessage = "Credenciales inválidas (${response.status.value}).",
        )
        return response.body<LoginResponse>().enrollmentToken
    }

    override suspend fun requestInitialToken(
        enrollmentToken: String,
        deviceId: String,
    ): TokenPair {
        val response = httpClient.post("$baseUrl/api/v1/auth/biometric/initial-token") {
            contentType(ContentType.Application.Json)
            setBody(
                InitialTokenRequest(
                    enrollmentToken = enrollmentToken,
                    deviceId = deviceId,
                )
            )
        }
        ensureSuccess(
            response = response,
            unauthorizedMessage = "Sesión revocada o expirada, inicia sesión nuevamente",
        )
        val dto = response.body<TokenPairResponse>()
        return TokenPair(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
        )
    }

    override suspend fun requestRefreshToken(
        refreshToken: String,
        deviceId: String,
    ): TokenPair {
        val response = httpClient.post("$baseUrl/api/v1/auth/biometric/refresh") {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshTokenRequest(
                    refreshToken = refreshToken,
                    deviceId = deviceId,
                )
            )
        }
        ensureSuccess(
            response = response,
            unauthorizedMessage = "Sesión revocada o expirada, inicia sesión nuevamente",
        )
        val dto = response.body<TokenPairResponse>()
        return TokenPair(
            accessToken = dto.accessToken,
            refreshToken = dto.refreshToken,
        )
    }

    override suspend fun executeProtectedRequest(
        accessToken: String?,
        simulateUnauthorized: Boolean,
    ): String {
        val response = httpClient.get("$baseUrl/api/protected-resource") {
            if (simulateUnauthorized) {
                header("X-Simulate-401", "true")
            } else if (!accessToken.isNullOrBlank()) {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }
        }
        ensureSuccess(
            response = response,
            unauthorizedMessage = "401 Unauthorized: Token expirado o inválido. Sesión bloqueada.",
        )
        return response.bodyAsText().ifBlank { "200 OK - Recurso protegido obtenido exitosamente." }
    }

    private suspend fun ensureSuccess(
        response: HttpResponse,
        unauthorizedMessage: String,
    ) {
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw UnauthorizedException(
                message = unauthorizedMessage,
                statusCode = response.status.value,
            )
        }
        if (!response.status.isSuccess()) {
            val errorBody = runCatching { response.bodyAsText() }.getOrNull().orEmpty().trim()
            val detail = if (errorBody.isNotBlank()) ": $errorBody" else ""
            throw IllegalStateException(
                "Error HTTP ${response.status.value} (${response.status.description})$detail"
            )
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://name-parameter-panama.ngrok-free.dev"
    }
}
