package gcd.testing.firstproject.signin.presentation.viewmodel

sealed interface LoginEvents {
    object OnSubmit: LoginEvents
    data class OnEmailChanged(val value: String): LoginEvents
    data class OnPasswordChanged(val value: String): LoginEvents
}