package gcd.testing.firstproject.profile.presentation.viewmodel

data class ProfileState(
    val loading: Boolean = false,
    val name: String,
    val email: String,
    val profilePicture: String? = null
)
