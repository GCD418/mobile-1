package gcd.testing.firstproject.biometrics.data.datasource

import gcd.testing.firstproject.biometrics.domain.model.TokenPair

interface BiometricRemoteDataSource {
    suspend fun fetchEnrollmentToken(
        deviceId: String = "",
        deviceName: String = "",
    ): String

    suspend fun requestInitialToken(
        enrollmentToken: String,
        deviceId: String = "",
    ): TokenPair

    suspend fun requestRefreshToken(
        refreshToken: String,
        deviceId: String = "",
    ): TokenPair

    suspend fun executeProtectedRequest(
        accessToken: String?,
        simulateUnauthorized: Boolean = false,
    ): String

    fun syncRefreshCount(count: Int) = Unit

    fun resetCounter() = Unit
}
