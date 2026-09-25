package gcd.testing.firstproject.userinformation.data.mapper

import gcd.testing.firstproject.userinformation.data.dto.UserInfoDto
import gcd.testing.firstproject.userinformation.domain.model.UserInfoModel

fun UserInfoDto.toDomain(): UserInfoModel = UserInfoModel(
    email = email?:"",
    company = "",
    avatarUrl = avatarUrl?:"",
    alias = ""
)
