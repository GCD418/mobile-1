package gcd.testing.firstproject.weather.data.repository

import gcd.testing.firstproject.weather.data.datasource.WeatherRemoteDatasource
import gcd.testing.firstproject.weather.data.mapper.toDomain
import gcd.testing.firstproject.weather.domain.model.WeatherModel
import gcd.testing.firstproject.weather.domain.repository.WeatherRepository

class WeatherRepositoryImpl(val datasource: WeatherRemoteDatasource) : WeatherRepository {
    override suspend fun getCurrentWeather(latitude: Float, longitude: Float): Result<WeatherModel> {
        return runCatching {
            datasource.getWeather(latitude, longitude).toDomain()
        }
    }
}
