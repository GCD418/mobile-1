package gcd.testing.firstproject.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcd.testing.firstproject.collaborativetasks.data.service.TaskAddedNotificationService
import gcd.testing.firstproject.isCollaborativeTaskFeatureEnabled
import gcd.testing.firstproject.collaborativetasks.presentation.screen.CollaborativeTasksScreen
import org.koin.compose.koinInject
import gcd.testing.firstproject.signin.presentation.screen.SigninScreen
import gcd.testing.firstproject.userinformation.presentation.screen.UserInformationScreen
import gcd.testing.firstproject.weather.presentation.screen.WeatherScreen
import gcd.testing.firstproject.dollar.presentation.screen.DollarScreen
import gcd.testing.firstproject.dollar.presentation.screen.ExchangeScreen

@Composable
fun AppNavHost(openTasksOnStart: Boolean = false) {
    val navController = rememberNavController()
    val collaborativeTasksEnabled = isCollaborativeTaskFeatureEnabled()

    if (collaborativeTasksEnabled) {
        val notificationService: TaskAddedNotificationService = koinInject()
        DisposableEffect(notificationService) {
            notificationService.start()
            onDispose { notificationService.stop() }
        }
    }

    val startDestination = if (openTasksOnStart && collaborativeTasksEnabled) {
        NavRoute.CollaborativeTasks
    } else {
        NavRoute.Exchange
    }
    NavHost(navController = navController, startDestination = startDestination) {
        composable<NavRoute.SignIn> {
            SigninScreen(navController)
        }

        composable<NavRoute.UserInformation> {
            UserInformationScreen()
        }

        composable<NavRoute.Weather> {
            WeatherScreen()
        }

        composable<NavRoute.Dollar> {
            DollarScreen()
        }

        if (collaborativeTasksEnabled) {
            composable<NavRoute.CollaborativeTasks> {
                CollaborativeTasksScreen()
            }
        }

        composable<NavRoute.Exchange> {
            ExchangeScreen(
                onOpenCollaborativeTasks = if (collaborativeTasksEnabled) {
                    { navController.navigate(NavRoute.CollaborativeTasks) }
                } else {
                    null
                },
            )
        }
    }
}