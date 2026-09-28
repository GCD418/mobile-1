package gcd.testing.firstproject.userinformation.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDto(
    val id: String,
    val title: String,

    @SerialName("poster_path")
    val posterPath: String,

)
