package gcd.testing.firstproject.weather.data.datasource

import gcd.testing.firstproject.weather.data.dto.WeatherDto
import gcd.testing.firstproject.weather.data.service.WeatherClient

class WeatherRemoteDatasource(val service: WeatherClient) {
    suspend fun getWeather(latitude: Float, longitude: Float): WeatherDto {
        return service.fetchWeather(latitude, longitude)
    }
}
