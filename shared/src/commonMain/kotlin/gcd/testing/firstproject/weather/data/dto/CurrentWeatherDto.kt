package gcd.testing.firstproject.weather.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherDto(
    val time: String,
    val temperature: Float,
    val windspeed: Float,
    val winddirection: Int,
    val weathercode: Int,
)
