package gcd.testing.firstproject.register.presentation.screen

sealed interface RegisterEffects {
    object NavigateToHome: RegisterEffects
    data class ShowError(val message: String): RegisterEffects
    data class ShowSuccess(val message: String): RegisterEffects
}