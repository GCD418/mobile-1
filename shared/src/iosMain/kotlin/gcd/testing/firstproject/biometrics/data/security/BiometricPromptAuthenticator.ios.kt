package gcd.testing.firstproject.biometrics.data.security

import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome

actual class BiometricPromptAuthenticator {
    actual suspend fun authenticate(title: String, subtitle: String): BiometricAuthOutcome =
        BiometricAuthOutcome.Success

    actual fun openSecuritySettings() = Unit
}
