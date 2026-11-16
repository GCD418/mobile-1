package gcd.testing.firstproject.biometrics.data.datasource

import gcd.testing.firstproject.biometrics.domain.model.TokenPair
import kotlinx.coroutines.delay
import kotlin.random.Random

class FakeBiometricRemoteDataSource {
    private var refreshCounter: Int = 0

    suspend fun fetchEnrollmentToken(): String {
        delay(1000)
        return "ENROLL_SEED_${randomId()}"
    }

    suspend fun requestInitialToken(enrollmentToken: String): TokenPair {
        require(enrollmentToken.isNotBlank()) { "El enrollmentToken no puede estar vacío." }
        delay(1000)
        refreshCounter = 0
        val id = randomId()
        return TokenPair(
            accessToken = "INITIAL_AT_$id",
            refreshToken = "RT_GEN_1_$id",
        )
    }

    suspend fun requestRefreshToken(refreshToken: String): TokenPair {
        require(refreshToken.isNotBlank()) { "El refreshToken no puede estar vacío." }
        delay(1000)
        val count = ++refreshCounter
        val id = randomId()
        return TokenPair(
            accessToken = "REFRESHED_AT_#${count}_$id",
            refreshToken = "RT_GEN_${count}_$id",
        )
    }

    fun syncRefreshCount(count: Int) {
        refreshCounter = count.coerceAtLeast(0)
    }

    fun resetCounter() {
        refreshCounter = 0
    }

    private fun randomId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
}
