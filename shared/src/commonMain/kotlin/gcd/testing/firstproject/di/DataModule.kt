package gcd.testing.firstproject.di

import gcd.testing.firstproject.userinformation.data.repository.GithubRepositoryImpl
import gcd.testing.firstproject.userinformation.domain.repository.GithubRepository
import gcd.testing.firstproject.weather.data.datasource.WeatherRemoteDatasource
import gcd.testing.firstproject.weather.data.repository.WeatherRepositoryImpl
import gcd.testing.firstproject.weather.data.service.WeatherClient
import gcd.testing.firstproject.weather.domain.repository.WeatherRepository
import org.koin.dsl.module

val dataModule = module {
    single<GithubRepository> { GithubRepositoryImpl() }
    single { WeatherClient() }
    single { WeatherRemoteDatasource(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
}