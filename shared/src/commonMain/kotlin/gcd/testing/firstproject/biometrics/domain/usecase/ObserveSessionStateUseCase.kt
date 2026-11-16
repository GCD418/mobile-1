package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveSessionStateUseCase(
    private val repository: BiometricSessionRepository,
) {
    operator fun invoke(): StateFlow<SessionState> = repository.sessionState
}
