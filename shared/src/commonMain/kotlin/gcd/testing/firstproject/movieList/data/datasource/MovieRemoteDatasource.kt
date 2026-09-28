package gcd.testing.firstproject.movieList.data.datasource

import gcd.testing.firstproject.movieList.data.service.MovieClient
import gcd.testing.firstproject.movieList.domain.model.MovieModel

class MovieRemoteDatasource(val service: MovieClient) {
    suspend fun getList():  Result<List<MovieModel>> {
        return service.fetchData()
    }
}