package gcd.testing.firstproject.movieDetails.domain.usecase

import gcd.testing.firstproject.movieDetails.domain.model.MovieDetail
import gcd.testing.firstproject.movieDetails.domain.repository.MovieDetailRepository
import gcd.testing.firstproject.movieDetails.domain.valueobject.MovieId

class GetMovieDetail (
    private val repository: MovieDetailRepository,
    private val movieId: MovieId
) {
    suspend fun invoke(): Result<MovieDetail> {
        return repository.findById(movieId)
    }
}