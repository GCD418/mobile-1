package gcd.testing.firstproject.biometrics.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val deviceId: String,
)

@Serializable
data class LoginResponse(
    val enrollmentToken: String,
)

@Serializable
data class EnrollBiometricsRequest(
    val deviceId: String,
    val deviceName: String,
)

@Serializable
data class EnrollBiometricsResponse(
    val enrollmentToken: String,
)

@Serializable
data class InitialTokenRequest(
    val enrollmentToken: String,
    val deviceId: String,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
    val deviceId: String,
)

@Serializable
data class TokenPairResponse(
    val accessToken: String,
    val refreshToken: String,
)
