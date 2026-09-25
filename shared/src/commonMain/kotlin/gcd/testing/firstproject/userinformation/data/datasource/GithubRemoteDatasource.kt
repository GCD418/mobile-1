package gcd.testing.firstproject.userinformation.data.datasource

import gcd.testing.firstproject.userinformation.data.dto.UserInfoDto

interface GithubRemoteDatasource {
    suspend fun getUser(nickname: String) : UserInfoDto
}