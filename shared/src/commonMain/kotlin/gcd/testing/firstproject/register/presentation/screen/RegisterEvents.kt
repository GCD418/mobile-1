package gcd.testing.firstproject.register.presentation.screen

sealed interface RegisterEvents {
    object OnSubmit: RegisterEvents
    object OnChangePassword: RegisterEvents
    object OnClickAlreadyRegistered: RegisterEvents
}