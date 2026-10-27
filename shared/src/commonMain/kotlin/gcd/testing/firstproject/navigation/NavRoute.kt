package gcd.testing.firstproject.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    object SignIn: NavRoute()
    @Serializable
    object UserInformation: NavRoute()

    @Serializable
    object Weather: NavRoute()

    @Serializable
    object Dollar: NavRoute()

    @Serializable
    object Exchange: NavRoute()

    @Serializable
    object CollaborativeTasks: NavRoute()
}