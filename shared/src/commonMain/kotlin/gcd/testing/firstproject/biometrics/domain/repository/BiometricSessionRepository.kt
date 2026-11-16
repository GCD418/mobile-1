package gcd.testing.firstproject.biometrics.domain.repository

import gcd.testing.firstproject.biometrics.domain.model.SessionState
import kotlinx.coroutines.flow.StateFlow

interface BiometricSessionRepository {
    val sessionState: StateFlow<SessionState>
    suspend fun enrollBiometrics(): Result<Unit>
    suspend fun unlockAndAuthenticate(): Result<Unit>
    fun lockSession()
    fun resetEnrollment()
}
