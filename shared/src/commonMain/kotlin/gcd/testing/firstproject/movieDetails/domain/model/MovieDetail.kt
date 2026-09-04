package gcd.testing.firstproject.movieDetails.domain.model

import gcd.testing.firstproject.movieDetails.domain.valueobject.MainCast
import gcd.testing.firstproject.movieDetails.domain.valueobject.MovieId
import gcd.testing.firstproject.movieDetails.domain.valueobject.PosterPath
import gcd.testing.firstproject.movieDetails.domain.valueobject.Score
import gcd.testing.firstproject.movieDetails.domain.valueobject.Summary

data class MovieDetail(
    val id: MovieId,
    val posterPath: PosterPath,
    val score: Score,
    val summary: Summary,
    val mainCast: MainCast
)
