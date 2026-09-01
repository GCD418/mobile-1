package gcd.testing.firstproject.profile.domain.usecase

import gcd.testing.firstproject.profile.domain.model.User
import gcd.testing.firstproject.profile.domain.repository.ProfileRepository
import gcd.testing.firstproject.profile.domain.valueobject.UserId

class GetUser (
    private val repository: ProfileRepository,
    private val userId: UserId
) {
    suspend fun invoke(): Result<User> {
        return repository.findById(userId)
    }
}