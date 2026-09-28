package gcd.testing.firstproject.weather.data.service

import gcd.testing.firstproject.weather.data.dto.WeatherDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WeatherClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    suspend fun fetchWeather(latitude: Float, longitude: Float): WeatherDto {
        val response = client.get(
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
        )
        try {
            return response.body<WeatherDto>()
        } catch (e: Exception) {
            throw e
        }
    }
}
