package gcd.testing.firstproject.movieList.domain.usecase

import gcd.testing.firstproject.movieList.domain.model.MovieModel
import gcd.testing.firstproject.movieList.domain.repository.MovieRepository

class GetPopularMoviesUseCase(val repository: MovieRepository) {
    suspend fun invoke(): Result<List<MovieModel>> {
        return repository.getMovies();
    }
}