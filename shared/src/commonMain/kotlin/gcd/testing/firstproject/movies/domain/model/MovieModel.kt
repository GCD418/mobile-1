package gcd.testing.firstproject.movies.domain.model

import gcd.testing.firstproject.movies.domain.vo.PosterPath

data class MovieModel(
    val title: String,
    val description: String,
    val posterPath: PosterPath
)