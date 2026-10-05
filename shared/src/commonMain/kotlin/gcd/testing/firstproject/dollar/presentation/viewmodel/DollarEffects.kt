package gcd.testing.firstproject.dollar.presentation.viewmodel

sealed interface DollarEffects {
    data class ShowError(val message: String) : DollarEffects
    data class ShowSuccess(val message: String) : DollarEffects
}
