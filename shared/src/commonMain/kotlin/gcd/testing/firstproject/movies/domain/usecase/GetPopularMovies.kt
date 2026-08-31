package gcd.testing.firstproject.movies.domain.usecase

import gcd.testing.firstproject.movies.domain.model.MovieModel
import gcd.testing.firstproject.movies.domain.repository.MovieRepository

class GetPopularMovies (
    private val repository: MovieRepository
) {
    suspend fun invoke(): List<MovieModel> {
        return repository.getMovies()
    }
}