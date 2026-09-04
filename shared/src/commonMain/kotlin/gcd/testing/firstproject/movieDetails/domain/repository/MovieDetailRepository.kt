package gcd.testing.firstproject.movieDetails.domain.repository

import gcd.testing.firstproject.movieDetails.domain.model.MovieDetail
import gcd.testing.firstproject.movieDetails.domain.valueobject.MovieId

interface MovieDetailRepository {
    suspend fun findById(id: MovieId): Result<MovieDetail>
}