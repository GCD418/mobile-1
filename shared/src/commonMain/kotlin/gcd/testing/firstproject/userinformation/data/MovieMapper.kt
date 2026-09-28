package gcd.testing.firstproject.userinformation.data

import gcd.testing.firstproject.movieList.domain.model.MovieModel
import gcd.testing.firstproject.userinformation.data.dto.MovieDto

fun MovieDto.toModel() : MovieModel {
    return MovieModel(id = id, title = title, posterPath = posterPath)
}
