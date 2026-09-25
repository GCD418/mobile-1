package gcd.testing.firstproject.userinformation.data.dto

import kotlinx.serialization.SerialName

data class UserInfoDto(
    val email: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,
)
