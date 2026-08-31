package gcd.testing.firstproject.movies.domain.repository

import gcd.testing.firstproject.movies.domain.model.MovieModel

interface MovieRepository {
    suspend fun getMovies(): List<MovieModel>
}