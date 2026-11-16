package gcd.testing.firstproject.biometrics.presentation.viewmodel

import gcd.testing.firstproject.biometrics.domain.model.SessionState

data class BiometricSessionState(
    val sessionState: SessionState = SessionState.Unregistered,
    val isLoading: Boolean = false,
    val loadingMessage: String? = null,
    val error: String? = null,
)
