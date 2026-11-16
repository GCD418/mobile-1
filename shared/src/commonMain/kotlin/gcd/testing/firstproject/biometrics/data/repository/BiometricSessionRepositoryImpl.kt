package gcd.testing.firstproject.biometrics.data.repository

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.biometrics.KSafeBiometrics
import gcd.testing.firstproject.biometrics.data.datasource.FakeBiometricRemoteDataSource
import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BiometricSessionRepositoryImpl(
    private val ksafe: KSafe,
    private val remoteDataSource: FakeBiometricRemoteDataSource,
) : BiometricSessionRepository {

    private val _sessionState = MutableStateFlow(resolveLockedOrUnregisteredState())
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        remoteDataSource.syncRefreshCount(getRefreshCount())
    }

    override suspend fun enrollBiometrics(): Result<Unit> = runCatching {
        val enrollmentToken = remoteDataSource.fetchEnrollmentToken()
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

        val isAuthenticated = KSafeBiometrics.verifyBiometric(
            reason = "Confirma tu identidad para desbloquear la sesión",
            allowDeviceCredentialFallback = true,
            title = "Autenticación Biométrica",
            cancelLabel = "Cancelar",
        )

        if (!isAuthenticated) {
            throw IllegalStateException("Autenticación biométrica cancelada o no verificada.")
        }

        if (refreshToken == null && enrollmentToken != null) {
            // Instancia 1: Primer Desbloqueo Biométrico -> solicita Token Inicial con el enrollmentToken
            val tokenPair = remoteDataSource.requestInitialToken(enrollmentToken)
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
            val tokenPair = remoteDataSource.requestRefreshToken(refreshToken)
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

    private companion object {
        const val KEY_ENROLLMENT_TOKEN = "ksafe_biometric_enrollment_token"
        const val KEY_REFRESH_TOKEN = "ksafe_biometric_refresh_token"
        const val KEY_REFRESH_COUNT = "ksafe_biometric_refresh_count"
    }
}
