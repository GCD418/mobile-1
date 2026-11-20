package gcd.testing.firstproject.biometrics.presentation.viewmodel

sealed interface BiometricSessionEvent {
    data object EnrollBiometrics : BiometricSessionEvent
    data object UnlockWithBiometrics : BiometricSessionEvent
    data object LockSession : BiometricSessionEvent
    data object OnAppBackgrounded : BiometricSessionEvent
    data object ResetEnrollment : BiometricSessionEvent
    data object OpenSecuritySettings : BiometricSessionEvent
    data class ToggleSimulateUnauthorized(val enabled: Boolean) : BiometricSessionEvent
    data class SimulateProtectedApiRequest(val forceUnauthorized: Boolean? = null) : BiometricSessionEvent
    data object ClearError : BiometricSessionEvent
}
