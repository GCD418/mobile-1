package gcd.testing.firstproject.biometrics.data.datasource

import gcd.testing.firstproject.biometrics.data.dto.EnrollBiometricsRequest
import gcd.testing.firstproject.biometrics.data.dto.EnrollBiometricsResponse
import gcd.testing.firstproject.biometrics.data.dto.InitialTokenRequest
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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class KtorBiometricRemoteDataSource(
    private val httpClient: HttpClient,
    private val baseUrl: String = DEFAULT_BASE_URL,
) : BiometricRemoteDataSource {

    override suspend fun fetchEnrollmentToken(
        deviceId: String,
        deviceName: String,
    ): String {
        val response = httpClient.post("$baseUrl/auth/enroll-biometrics") {
            contentType(ContentType.Application.Json)
            setBody(
                EnrollBiometricsRequest(
                    deviceId = deviceId,
                    deviceName = deviceName,
                )
            )
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw UnauthorizedException(
                message = "No autorizado para enrolar biometría (${response.status.value}).",
                statusCode = response.status.value,
            )
        }
        return response.body<EnrollBiometricsResponse>().enrollmentToken
    }

    override suspend fun requestInitialToken(
        enrollmentToken: String,
        deviceId: String,
    ): TokenPair {
        val response = httpClient.post("$baseUrl/auth/biometric/initial-token") {
            contentType(ContentType.Application.Json)
            setBody(
                InitialTokenRequest(
                    enrollmentToken = enrollmentToken,
                    deviceId = deviceId,
                )
            )
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw UnauthorizedException(
                message = "Sesión revocada o expirada, inicia sesión nuevamente",
                statusCode = response.status.value,
            )
        }
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
        val response = httpClient.post("$baseUrl/auth/biometric/refresh") {
            contentType(ContentType.Application.Json)
            setBody(
                RefreshTokenRequest(
                    refreshToken = refreshToken,
                    deviceId = deviceId,
                )
            )
        }
        if (response.status == HttpStatusCode.Unauthorized || response.status == HttpStatusCode.Forbidden) {
            throw UnauthorizedException(
                message = "Sesión revocada o expirada, inicia sesión nuevamente",
                statusCode = response.status.value,
            )
        }
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
        if (response.status == HttpStatusCode.Unauthorized) {
            throw UnauthorizedException(
                message = "401 Unauthorized: Token expirado o inválido. Sesión bloqueada.",
                statusCode = HttpStatusCode.Unauthorized.value,
            )
        }
        return response.bodyAsText().ifBlank { "200 OK - Recurso protegido obtenido exitosamente." }
    }

    companion object {
        const val DEFAULT_BASE_URL = "https://name-parameter-panama.ngrok-free.dev"
    }
}
