package gcd.testing.firstproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcd.testing.firstproject.signin.presentation.screen.SigninScreen
import gcd.testing.firstproject.userinformation.presentation.screen.UserInformationScreen
import gcd.testing.firstproject.weather.presentation.screen.WeatherScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.Weather) {
        composable<NavRoute.SignIn> {
            SigninScreen(navController)
        }

        composable<NavRoute.UserInformation> {
            UserInformationScreen()
        }

        composable<NavRoute.Weather> {
            WeatherScreen()
        }
    }
}