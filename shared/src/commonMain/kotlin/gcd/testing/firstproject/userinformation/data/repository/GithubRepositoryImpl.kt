package gcd.testing.firstproject.userinformation.data.repository

import gcd.testing.firstproject.userinformation.domain.model.UserInfoModel
import gcd.testing.firstproject.userinformation.domain.repository.GithubRepository

class GithubRepositoryImpl: GithubRepository {
    override suspend fun findByAlias(alias: String): Result<UserInfoModel> {
        return Result.success(UserInfoModel(
            email = "gcd418@gmail.com",
            company = "aitbol SRL",
            avatarUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRTJbyryLwQznMC7X4Q4TcPN-T5KP3vb_etvCEYL7zccQ&s=10",
            alias = "GCD418"
        ))
    }
}