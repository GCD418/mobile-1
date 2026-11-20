package gcd.testing.firstproject.biometrics.domain.usecase

import gcd.testing.firstproject.biometrics.domain.repository.BiometricSessionRepository

class OpenSecuritySettingsUseCase(
    private val repository: BiometricSessionRepository,
) {
    operator fun invoke() = repository.openSecuritySettings()
}
