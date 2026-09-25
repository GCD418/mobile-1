package gcd.testing.firstproject.userinformation.data.service

import gcd.testing.firstproject.userinformation.data.datasource.GithubRemoteDatasource
import gcd.testing.firstproject.userinformation.data.dto.UserInfoDto
import kotlinx.serialization.json.Json

class GithubApiService : GithubRemoteDatasource {
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
    override suspend fun getUser(nickname: String): UserInfoDto {
        val response = client.get("https://api.github.com/users/$nickname")
        try {
            return response.body<UserInfoDto>()
        } catch (e: Exception) {
            throw e
        }
    }
}