package gcd.testing.firstproject.biometrics.presentation.viewmodel

sealed interface BiometricSessionEvent {
    data object EnrollBiometrics : BiometricSessionEvent
    data object UnlockWithBiometrics : BiometricSessionEvent
    data object LockSession : BiometricSessionEvent
    data object ResetEnrollment : BiometricSessionEvent
    data object ClearError : BiometricSessionEvent
}
