package gcd.testing.firstproject.userinformation.presentation.viewmodel

sealed interface UserInformationEffects {
    data class ShowToast(val message: String): UserInformationEffects
    object OnSuccess: UserInformationEffects
    object NavigateToBack: UserInformationEffects
}