package gcd.testing.firstproject.movieList.data.service

import gcd.testing.firstproject.movieList.domain.model.MovieModel
import gcd.testing.firstproject.userinformation.data.dto.MovieResponseDto
import gcd.testing.firstproject.userinformation.data.dto.UserInfoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class MovieClient {
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
    suspend fun fetchData(): Result<List<MovieModel>> {
        val response = client.get("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=fa3e844ce31744388e07fa47c7c5d8c3")
        try {
            val body = response.body<MovieResponseDto>()
            return Result.success(body.results.map{ it.toModel()})
        } catch (e: Exception) {
            throw e
        }
    }
}