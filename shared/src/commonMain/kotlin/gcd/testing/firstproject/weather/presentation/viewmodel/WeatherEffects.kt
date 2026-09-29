package gcd.testing.firstproject.weather.presentation.viewmodel

sealed interface WeatherEffects {
    data class ShowError(val message: String) : WeatherEffects
    object NavigateBack : WeatherEffects
}
