package gcd.testing.firstproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcd.testing.firstproject.signin.presentation.screen.SigninScreen
import gcd.testing.firstproject.userinformation.presentation.screen.UserInformationScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.SignIn) {
        composable<NavRoute.SignIn> {
            SigninScreen(navController)
        }

        composable<NavRoute.UserInformation> {
            UserInformationScreen()
        }
    }
}