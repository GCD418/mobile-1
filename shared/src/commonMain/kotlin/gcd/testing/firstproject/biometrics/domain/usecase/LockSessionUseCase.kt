package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository

class LockSessionUseCase(
    private val repository: BiometricSessionRepository,
) {
    operator fun invoke() = repository.lockSession()
}
