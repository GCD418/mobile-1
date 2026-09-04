package gcd.testing.firstproject.profile.presentation.viewmodel

sealed interface ProfileEvents {
    object OnLogOut: ProfileEvents
    object OnClickSettings: ProfileEvents
    object OnBack: ProfileEvents
}