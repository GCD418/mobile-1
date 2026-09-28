package gcd.testing.firstproject.weather.domain.model

data class WeatherModel(
    val temperature: Float,
    val windSpeed: Float,
    val windDirection: Int,
    val weatherCode: Int,
    val latitude: Float,
    val longitude: Float,
)
