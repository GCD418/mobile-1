package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository

class ResetEnrollmentUseCase(
    private val repository: BiometricSessionRepository,
) {
    operator fun invoke() = repository.resetEnrollment()
}
