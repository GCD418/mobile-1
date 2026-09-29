package gcd.testing.firstproject.di

import gcd.testing.firstproject.signin.presentation.viewmodel.LoginViewModel
import gcd.testing.firstproject.userinformation.presentation.viewmodel.UserInformationViewModel
import gcd.testing.firstproject.weather.presentation.viewmodel.WeatherViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::UserInformationViewModel)
    viewModelOf(::WeatherViewModel)
}
