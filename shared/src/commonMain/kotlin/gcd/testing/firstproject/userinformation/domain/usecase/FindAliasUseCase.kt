package gcd.testing.firstproject.userinformation.domain.usecase

import gcd.testing.firstproject.userinformation.domain.model.UserInfoModel
import gcd.testing.firstproject.userinformation.domain.repository.GithubRepository

class FindAliasUseCase (
    val repository: GithubRepository
) {
    suspend fun invoke(alias: String): Result<UserInfoModel> { //This is default behavior
        return repository.findByAlias(alias)
    }
}