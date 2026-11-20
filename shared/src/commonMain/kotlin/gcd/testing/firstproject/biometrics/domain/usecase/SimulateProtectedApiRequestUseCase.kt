package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository

class SimulateProtectedApiRequestUseCase(
    private val repository: BiometricSessionRepository,
) {
    suspend operator fun invoke(forceUnauthorized: Boolean = false): Result<String> =
        repository.simulateProtectedApiRequest(forceUnauthorized = forceUnauthorized)
}
