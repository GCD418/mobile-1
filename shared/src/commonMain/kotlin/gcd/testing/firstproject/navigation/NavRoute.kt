package gcd.testing.firstproject.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    object SignIn: NavRoute()
    @Serializable
    object UserInformation: NavRoute()
}