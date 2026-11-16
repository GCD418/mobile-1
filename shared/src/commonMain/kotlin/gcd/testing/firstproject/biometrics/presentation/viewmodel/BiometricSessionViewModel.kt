package gcd.testing.firstproject.biometrics.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.biometrics.domain.usecase.EnrollBiometricsUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.LockSessionUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ObserveSessionStateUseCase
import gcd.testing.firstproject.biometrics.domain.usecase.ResetEnrollmentUseCase
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
) : ViewModel() {

    private val _state = MutableStateFlow(
        BiometricSessionState(sessionState = observeSessionState().value),
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeSessionState().collect { sessionState ->
                _state.update { it.copy(sessionState = sessionState) }
            }
        }
    }

    fun emitEvent(event: BiometricSessionEvent) {
        when (event) {
            BiometricSessionEvent.EnrollBiometrics -> handleEnrollBiometrics()
            BiometricSessionEvent.UnlockWithBiometrics -> handleUnlockWithBiometrics()
            BiometricSessionEvent.LockSession -> {
                _state.update { it.copy(error = null) }
                lockSession()
            }
            BiometricSessionEvent.ResetEnrollment -> {
                _state.update { it.copy(error = null) }
                resetEnrollment()
            }
            BiometricSessionEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun handleEnrollBiometrics() {
        if (_state.value.isLoading) return
        _state.update {
            it.copy(
                isLoading = true,
                loadingMessage = "Solicitando enrollmentToken al Mock Backend...",
                error = null,
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
                loadingMessage = "Verificando biometría y autenticando con Mock Backend...",
                error = null,
            )
        }
        viewModelScope.launch {
            val result = unlockWithBiometrics()
            _state.update { current ->
                current.copy(
                    isLoading = false,
                    loadingMessage = null,
                    error = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
