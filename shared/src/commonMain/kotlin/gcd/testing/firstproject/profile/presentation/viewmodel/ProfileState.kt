package gcd.testing.firstproject.profile.presentation.viewmodel

import gcd.testing.firstproject.movies.domain.model.MovieModel

data class ProfileState(
    val loading: Boolean = false,
    val name: String,
    val email: String,
    val profilePicture: String? = null,
    val favoriteMovies: List<MovieModel>

)
