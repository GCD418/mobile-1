package gcd.testing.firstproject.profile.domain.repository

import gcd.testing.firstproject.profile.domain.model.User
import gcd.testing.firstproject.profile.domain.valueobject.UserId

interface ProfileRepository {
    suspend fun findById(id: UserId): Result<User>
}