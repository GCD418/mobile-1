package gcd.testing.firstproject.biometrics.presentation.viewmodel

import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome
import gcd.testing.firstproject.biometrics.domain.model.SessionState

data class BiometricSessionState(
    val sessionState: SessionState = SessionState.Unregistered,
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,
    val error: String? = null,
    val biometricOutcome: BiometricAuthOutcome? = null,
    val protectedApiResponse: String? = null,
    val simulateUnauthorizedNextCall: Boolean = false,
)
