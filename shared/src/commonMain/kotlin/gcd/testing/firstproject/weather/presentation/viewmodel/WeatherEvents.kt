package gcd.testing.firstproject.weather.presentation.viewmodel

sealed interface WeatherEvents {
    data class OnLatitudeChange(val value: String) : WeatherEvents
    data class OnLongitudeChange(val value: String) : WeatherEvents
    object OnSubmit : WeatherEvents
    object OnBack : WeatherEvents
}
