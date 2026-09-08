package gcd.testing.firstproject.userinformation.domain.repository

import gcd.testing.firstproject.userinformation.domain.model.UserInfoModel

interface GithubRepository {
    suspend fun findByAlias(alias: String): Result<UserInfoModel>
}