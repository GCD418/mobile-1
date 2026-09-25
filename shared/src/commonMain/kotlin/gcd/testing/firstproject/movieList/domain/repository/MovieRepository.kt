package gcd.testing.firstproject.movieList.domain.repository

import gcd.testing.firstproject.movieList.domain.model.MovieModel

interface MovieRepository {
    suspend fun getMovies(): Result<List<MovieModel>>
}