package gcd.testing.firstproject.biometrics.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthException
import gcd.testing.firstproject.biometrics.domain.model.BiometricAuthOutcome
import gcd.testing.firstproject.biometrics.domain.model.SessionState
import gcd.testing.firstproject.biometrics.domain.usecase.EnrollBiometricsUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.LockSessionUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ObserveSessionStateUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.OpenSecuritySettingsUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ResetEnrollmentUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.SimulateProtectedApiRequestUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.UnlockWithBiometricsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BiometricSessionViewModel(
    private val observeSessionState: ObserveSessionStateUseCase,
    private val enrollBiometrics: EnrollBiometricsUseCase,
    private val unlockWithBiometrics: UnlockWithBiometricsUseCase,
    private val lockSession: LockSessionUseCase,
    private val resetEnrollment: ResetEnrollmentUseCase,
    private val simulateProtectedApiRequest: SimulateProtectedApiRequestUseCase,
    private val openSecuritySettings: OpenSecuritySettingsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(
        BiometricSessionState(sessionState = observeSessionState().value),
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionState().collect { sessionState ->
                _state.update { current ->
                    current.copy(
                        sessionState = sessionState,
                        protectedApiResponse = if (sessionState is SessionState.Unlocked) {
                            current.protectedApiResponse
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }

    fun emitEvent(event: BiometricSessionEvent) {
        when (event) {
            BiometricSessionEvent.EnrollBiometrics -> handleEnrollBiometrics()
            BiometricSessionEvent.UnlockWithBiometrics -> handleUnlockWithBiometrics()
            BiometricSessionEvent.LockSession -> {
                _state.update {
                    it.copy(
                        error = null,
                        biometricOutcome = null,
                        protectedApiResponse = null,
                    )
                }
                lockSession()
            }
            BiometricSessionEvent.OnAppBackgrounded -> {
                val wasUnlocked = _state.value.sessionState is SessionState.Unlocked
                lockSession()
                if (wasUnlocked) {
                    _state.update {
                        it.copy(
                            biometricOutcome = null,
                            protectedApiResponse = null,
                            error = "La sesión se bloqueó automáticamente al pasar a segundo plano (ON_STOP) y el accessToken fue purgado de la memoria RAM.",
                        )
                    }
                }
            }
            BiometricSessionEvent.ResetEnrollment -> {
                _state.update {
                    it.copy(
                        error = null,
                        biometricOutcome = null,
                        protectedApiResponse = null,
                    )
                }
                resetEnrollment()
            }
            BiometricSessionEvent.OpenSecuritySettings -> {
                openSecuritySettings()
            }
            is BiometricSessionEvent.ToggleSimulateUnauthorized -> {
                _state.update { it.copy(simulateUnauthorizedNextCall = event.enabled) }
            }
            is BiometricSessionEvent.SimulateProtectedApiRequest -> {
                handleSimulateProtectedApiRequest(event.forceUnauthorized)
            }
            BiometricSessionEvent.ClearError -> {
                _state.update {
                    it.copy(
                        error = null,
                        biometricOutcome = null,
                    )
                }
            }
        }
    }

    private fun handleEnrollBiometrics() {
        if (_state.value.isLoading) return
        _state.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Solicitando enrollmentToken al Backend...",
                error = null,
                biometricOutcome = null,
            )
        }
        viewModelScope.launch {
            val result = enrollBiometrics()
            _state.update { current ->
                current.copy(
                    isLoading = false,
                    loadingMessage = null,
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }

    private fun handleUnlockWithBiometrics() {
        if (_state.value.isLoading) return
        _state.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Verificando biometría (Rostro 2D / Huella) y autenticando...",
                error = null,
                biometricOutcome = null,
            )
        }
        viewModelScope.launch {
            val result = unlockWithBiometrics()
            val exception = result.exceptionOrNull()
            val outcome = if (exception == null) {
                BiometricAuthOutcome.Success
            } else {
                (exception as? BiometricAuthException)?.outcome
            }
            _state.update { current ->
                current.copy(
                    isLoading = false,
                    loadingMessage = null,
                    biometricOutcome = outcome,
                    error = if (outcome != null && outcome != BiometricAuthOutcome.Success) {
                        null
                    } else {
                        exception?.message
                    },
                )
            }
        }
    }

    private fun handleSimulateProtectedApiRequest(forceUnauthorizedOverride: Boolean?) {
        if (_state.value.isLoading) return
        val forceUnauthorized = forceUnauthorizedOverride ?: _state.value.simulateUnauthorizedNextCall
        _state.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Ejecutando petición a API protegida con Bearer Token...",
                error = null,
                protectedApiResponse = null,
            )
        }
        viewModelScope.launch {
            val result = simulateProtectedApiRequest(forceUnauthorized = forceUnauthorized)
            _state.update { current ->
                current.copy(
                    isLoading = false,
                    loadingMessage = null,
                    protectedApiResponse = result.getOrNull(),
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
