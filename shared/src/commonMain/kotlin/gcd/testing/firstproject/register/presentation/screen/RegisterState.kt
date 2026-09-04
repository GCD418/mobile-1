package gcd.testing.firstproject.register.presentation.screen

data class RegisterState (
    val loading: Boolean = false,
    val name: String,
    val email: String,
)