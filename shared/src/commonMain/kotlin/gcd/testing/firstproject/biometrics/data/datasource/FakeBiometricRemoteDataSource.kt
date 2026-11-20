package gcd.testing.firstproject.biometrics.data.datasource

import gcd.testing.firstproject.biometrics.domain.model.TokenPair
import gcd.testing.firstproject.biometrics.domain.model.UnauthorizedException
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeBiometricRemoteDataSource : BiometricRemoteDataSource {
    private var refreshCounter: Int = 0

    override suspend fun fetchEnrollmentToken(
        deviceId: String,
        deviceName: String,
    ): String {
        delay(1000)
        return "ENROLL_SEED_${randomId()}"
    }

    override suspend fun requestInitialToken(
        enrollmentToken: String,
        deviceId: String,
    ): TokenPair {
        require(enrollmentToken.isNotBlank()) { "El enrollmentToken no puede estar vacío." }
        delay(1000)
        refreshCounter = 0
        val id = randomId()
        return TokenPair(
            accessToken = "INITIAL_AT_$id",
            refreshToken = "RT_GEN_1_$id",
        )
    }

    override suspend fun requestRefreshToken(
        refreshToken: String,
        deviceId: String,
    ): TokenPair {
        require(refreshToken.isNotBlank()) { "El refreshToken no puede estar vacío." }
        delay(1000)
        val count = ++refreshCounter
        val id = randomId()
        return TokenPair(
            accessToken = "REFRESHED_AT_#${count}_$id",
            refreshToken = "RT_GEN_${count}_$id",
        )
    }

    override suspend fun executeProtectedRequest(
        accessToken: String?,
        simulateUnauthorized: Boolean,
    ): String {
        delay(700)
        if (simulateUnauthorized || accessToken.isNullOrBlank()) {
            throw UnauthorizedException(
                message = "HTTP 401 Unauthorized en endpoint protegido: el accessToken expiró. La sesión se bloqueó automáticamente; desbloquea con biometría para rotar el Refresh Token.",
                statusCode = 401,
            )
        }
        return "200 OK - Petición API Protegida autenticada con Authorization: Bearer $accessToken"
    }

    override fun syncRefreshCount(count: Int) {
        refreshCounter = count.coerceAtLeast(0)
    }

    override fun resetCounter() {
        refreshCounter = 0
    }

    private fun randomId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
