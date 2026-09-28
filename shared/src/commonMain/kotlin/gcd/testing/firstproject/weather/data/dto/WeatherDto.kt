package gcd.testing.firstproject.weather.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val latitude: Float,
    val longitude: Float,
    @SerialName("current_weather")
    val currentWeather: CurrentWeatherDto,
)
