package gcd.testing.firstproject.signin.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcd.testing.firstproject.signin.presentation.viewmodel.LoginEffects.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffects>()
    val effects = _effects.asSharedFlow()

    private fun emitEffect(effect: LoginEffects) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    fun emitEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.OnEmailChanged -> {

            }

            is LoginEvents.OnPasswordChanged -> {

            }

            LoginEvents.OnSubmit -> {
                var isValid = true
                if (state.value.email.isBlank()) {
                    emitEffect(ShowToast("The email field is required"))
                    isValid = false
                } else if (state.value.password.isBlank()) {
                    emitEffect(ShowToast("You can't login without a password"))
                    isValid = false
                }
                if (isValid) {
                    emitEffect(LoginEffects.NavigateToHome)
                }
            }

            LoginEvents.OnGithub -> {
                emitEffect(LoginEffects.NavigateToGithub)
            }
        }
    }
}