package gcd.testing.firstproject.biometrics.data.security

import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome

expect class BiometricPromptAuthenticator {
    suspend fun authenticate(title: String, subtitle: String): BiometricAuthOutcome
    fun openSecuritySettings()
}
