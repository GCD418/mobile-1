package gcd.testing.firstproject.biometrics.data.repository

import eu.anifantakis.lib.ksafe.KSafe
import gcd.testing.firstproject.biometrics.data.datasource.BiometricRemoteDataSource
import gcd.testing.firstproject.biometrics.data.security.BiometricPromptAuthenticator
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthException
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome
import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.model.UnauthorizedException
import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository
import gcd.testing.firstproject.getPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class BiometricSessionRepositoryImpl(
    private val ksafe: KSafe,
    private val remoteDataSource: BiometricRemoteDataSource,
    private val biometricAuthenticator: BiometricPromptAuthenticator,
) : BiometricSessionRepository {

    private val _sessionState = MutableStateFlow(resolveLockedOrUnregisteredState())
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        getOrCreateDeviceId()
        remoteDataSource.syncRefreshCount(getRefreshCount())
    }

    override suspend fun enrollBiometrics(): Result<Unit> = runCatching {
        val deviceId = getOrCreateDeviceId()
        val deviceName = getPlatform().name
        val enrollmentToken = remoteDataSource.fetchEnrollmentToken(
            deviceId = deviceId,
            deviceName = deviceName,
        )
        ksafe.putDirect(KEY_ENROLLMENT_TOKEN, enrollmentToken)
        ksafe.deleteDirect(KEY_REFRESH_TOKEN)
        ksafe.putDirect(KEY_REFRESH_COUNT, 0)
        remoteDataSource.resetCounter()

        _sessionState.value = SessionState.Locked(isFirstUnlockPending = true)
    }

    override suspend fun unlockAndAuthenticate(): Result<Unit> = runCatching {
        val enrollmentToken = getEnrollmentToken()
        val refreshToken = getRefreshToken()

        if (enrollmentToken == null && refreshToken == null) {
            _sessionState.value = SessionState.Unregistered
            throw IllegalStateException("No existe un enrolamiento activo. Realiza primero el enrolamiento inicial.")
        }

        val outcome = biometricAuthenticator.authenticate(
            title = "Autenticación Biométrica",
            subtitle = "Verifica tu rostro o huella para desbloquear la sesión",
        )

        if (outcome != BiometricAuthOutcome.Success) {
            throw BiometricAuthException(outcome)
        }

        val deviceId = getOrCreateDeviceId()

        try {
            if (refreshToken == null && enrollmentToken != null) {
                // Instancia 1: Primer Desbloqueo Biométrico -> solicita Token Inicial con el enrollmentToken
                val tokenPair = remoteDataSource.requestInitialToken(
                    enrollmentToken = enrollmentToken,
                    deviceId = deviceId,
                )
                ksafe.deleteDirect(KEY_ENROLLMENT_TOKEN)
                ksafe.putDirect(KEY_REFRESH_TOKEN, tokenPair.refreshToken)
                ksafe.putDirect(KEY_REFRESH_COUNT, 0)

                _sessionState.value = SessionState.Unlocked(
                    accessToken = tokenPair.accessToken,
                    authStageDescription = "Autenticado vía Token Inicial",
                    refreshCount = 0,
                )
            } else if (refreshToken != null) {
                // Instancia 2+: Siguientes Desbloqueos Biométricos -> rota el Refresh Token
                remoteDataSource.syncRefreshCount(getRefreshCount())
                val tokenPair = remoteDataSource.requestRefreshToken(
                    refreshToken = refreshToken,
                    deviceId = deviceId,
                )
                val newRefreshCount = getRefreshCount() + 1

                ksafe.deleteDirect(KEY_ENROLLMENT_TOKEN)
                ksafe.putDirect(KEY_REFRESH_TOKEN, tokenPair.refreshToken)
                ksafe.putDirect(KEY_REFRESH_COUNT, newRefreshCount)

                _sessionState.value = SessionState.Unlocked(
                    accessToken = tokenPair.accessToken,
                    authStageDescription = "Autenticado vía Refresh Token #$newRefreshCount",
                    refreshCount = newRefreshCount,
                )
            }
        } catch (e: UnauthorizedException) {
            // Si requestInitialToken o requestRefreshToken falla con HTTP 401 o 403,
            // se limpia KSafe y se regresa a SessionState.Unregistered.
            resetEnrollment()
            throw UnauthorizedException(
                message = "Sesión revocada o expirada, inicia sesión nuevamente",
                statusCode = e.statusCode,
            )
        }
    }

    override suspend fun simulateProtectedApiRequest(forceUnauthorized: Boolean): Result<String> = runCatching {
        val current = _sessionState.value
        if (current !is SessionState.Unlocked) {
            lockSession()
            throw UnauthorizedException(
                message = "La sesión está bloqueada. Desbloquea con biometría para realizar peticiones.",
                statusCode = 401,
            )
        }

        try {
            remoteDataSource.executeProtectedRequest(
                accessToken = current.accessToken,
                simulateUnauthorized = forceUnauthorized,
            )
        } catch (e: UnauthorizedException) {
            lockSession()
            throw e
        }
    }

    override fun lockSession() {
        // Descarta el accessToken en memoria RAM pasando a estado Locked
        _sessionState.value = resolveLockedOrUnregisteredState()
    }

    override fun resetEnrollment() {
        ksafe.deleteDirect(KEY_ENROLLMENT_TOKEN)
        ksafe.deleteDirect(KEY_REFRESH_TOKEN)
        ksafe.putDirect(KEY_REFRESH_COUNT, 0)
        remoteDataSource.resetCounter()
        _sessionState.value = SessionState.Unregistered
    }

    override fun openSecuritySettings() {
        biometricAuthenticator.openSecuritySettings()
    }

    private fun resolveLockedOrUnregisteredState(): SessionState {
        val refreshToken = getRefreshToken()
        val enrollmentToken = getEnrollmentToken()
        return when {
            refreshToken != null -> SessionState.Locked(isFirstUnlockPending = false)
            enrollmentToken != null -> SessionState.Locked(isFirstUnlockPending = true)
            else -> SessionState.Unregistered
        }
    }

    private fun getEnrollmentToken(): String? =
        ksafe.getDirect(KEY_ENROLLMENT_TOKEN, "").takeIf { it.isNotBlank() }

    private fun getRefreshToken(): String? =
        ksafe.getDirect(KEY_REFRESH_TOKEN, "").takeIf { it.isNotBlank() }

    private fun getRefreshCount(): Int =
        ksafe.getDirect(KEY_REFRESH_COUNT, 0)

    private fun getOrCreateDeviceId(): String {
        val existing = ksafe.getDirect(KEY_DEVICE_ID, "").takeIf { it.isNotBlank() }
        if (existing != null) return existing

        val chars = "abcdef0123456789"
        val randomSuffix = (1..12).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        val generated = "DEV_$randomSuffix"
        ksafe.putDirect(KEY_DEVICE_ID, generated)
        return generated
    }

    private companion object {
        const val KEY_ENROLLMENT_TOKEN = "ksafe_biometric_enrollment_token"
        const val KEY_REFRESH_TOKEN = "ksafe_biometric_refresh_token"
        const val KEY_REFRESH_COUNT = "ksafe_biometric_refresh_count"
        const val KEY_DEVICE_ID = "ksafe_biometric_device_id"
    }
}
