package gcd.testing.firstproject.biometrics.domain.model

sealed interface BiometricAuthOutcome {
    data object Success : BiometricAuthOutcome
    data object Cancelled : BiometricAuthOutcome
    data object NoBiometricsEnrolled : BiometricAuthOutcome
    data object Lockout : BiometricAuthOutcome
    data object HardwareUnavailable : BiometricAuthOutcome
    data class Error(val message: String) : BiometricAuthOutcome
}
