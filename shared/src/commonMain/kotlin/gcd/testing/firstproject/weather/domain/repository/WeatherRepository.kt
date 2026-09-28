package gcd.testing.firstproject.weather.domain.repository

import gcd.testing.firstproject.weather.domain.model.WeatherModel

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Float, longitude: Float): Result<WeatherModel>
}
