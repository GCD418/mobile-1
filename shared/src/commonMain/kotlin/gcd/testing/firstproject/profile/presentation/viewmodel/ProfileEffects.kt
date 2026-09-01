package gcd.testing.firstproject.profile.presentation.viewmodel

sealed interface ProfileEffects {
    object NavigateToHome: ProfileEffects
    object ShowFavoriteMovies: ProfileEffects
    object ShowLogoutConfirmation: ProfileEffects
}