package gcd.testing.firstproject.movieList.data.repository

import gcd.testing.firstproject.movieList.data.datasource.MovieRemoteDatasource
import gcd.testing.firstproject.movieList.domain.model.MovieModel
import gcd.testing.firstproject.movieList.domain.repository.MovieRepository

class MovieRepositoryImpl (val datasource: MovieRemoteDatasource) : MovieRepository {
    override suspend fun getMovies(): Result<List<MovieModel>> {
        return datasource.getList()
    }

}