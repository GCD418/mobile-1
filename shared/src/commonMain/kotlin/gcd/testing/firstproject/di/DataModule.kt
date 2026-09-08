package gcd.testing.firstproject.di

import gcd.testing.firstproject.userinformation.data.repository.GithubRepositoryImpl
import gcd.testing.firstproject.userinformation.domain.repository.GithubRepository
import org.koin.dsl.module

val dataModule = module {
    single<GithubRepository>{GithubRepositoryImpl()}
}