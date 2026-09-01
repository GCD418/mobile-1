package gcd.testing.firstproject.profile.domain.model

import gcd.testing.firstproject.profile.domain.valueobject.Email
import gcd.testing.firstproject.profile.domain.valueobject.UserId
import gcd.testing.firstproject.profile.domain.valueobject.Name

data class User(
    val id: UserId,
    val email: Email,
    val name: Name
)
