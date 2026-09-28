package gcd.testing.firstproject.weather.data.mapper

import gcd.testing.firstproject.weather.data.dto.WeatherDto
import gcd.testing.firstproject.weather.domain.model.WeatherModel

fun WeatherDto.toDomain(): WeatherModel = WeatherModel(
    temperature = currentWeather.temperature,
    windSpeed = currentWeather.windspeed,
    windDirection = currentWeather.winddirection,
    weatherCode = currentWeather.weathercode,
    time = currentWeather.time,
    latitude = latitude,
    longitude = longitude,
)
