package gcd.testing.firstproject.userinformation.presentation.viewmodel

sealed interface UserInformationEvents {
    object OnBack : UserInformationEvents
    object OnSubmit : UserInformationEvents
    data class OnAliasChange(val value: String) : UserInformationEvents
}