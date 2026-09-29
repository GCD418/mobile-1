package gcd.testing.firstproject.weather.presentation.viewmodel

import gcd.testing.firstproject.weather.domain.model.WeatherModel

data class WeatherState(
    val latitude: String = "",
    val longitude: String = "",
    val isLoading: Boolean = false,
    val weather: WeatherModel? = null,
    val error: String? = null,
)
