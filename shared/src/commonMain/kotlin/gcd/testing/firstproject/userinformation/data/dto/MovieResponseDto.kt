package gcd.testing.firstproject.userinformation.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponseDto(
    val page: Int,
    val results: List<MovieDto>
)
