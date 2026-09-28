package gcd.testing.firstproject.weather.domain.usecase

import gcd.testing.firstproject.weather.domain.model.WeatherModel
import gcd.testing.firstproject.weather.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(val repository: WeatherRepository) {
    suspend fun invoke(latitude: Float, longitude: Float): Result<WeatherModel> {
        return repository.getCurrentWeather(latitude, longitude)
    }
}
