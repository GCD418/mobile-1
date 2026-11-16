package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository

class UnlockWithBiometricsUseCase(
    private val repository: BiometricSessionRepository,
) {
    suspend operator fun invoke(): Result<Unit> = repository.unlockAndAuthenticate()
}
