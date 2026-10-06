package gcd.testing.firstproject.di

import gcd.testing.firstproject.config.AppDatabase
import gcd.testing.firstproject.dollar.data.dao.DollarDao
import gcd.testing.firstproject.dollar.data.datasource.DollarLocalDataSource
import gcd.testing.firstproject.dollar.data.datasource.RealTimeDataBase
import gcd.testing.firstproject.dollar.data.repository.DollarRepositoryImpl
import gcd.testing.firstproject.dollar.data.repository.ExchangeRepositoryImpl
import gcd.testing.firstproject.dollar.domain.repository.DollarRepository
import gcd.testing.firstproject.dollar.domain.repository.ExchangeRepository
import gcd.testing.firstproject.userinformation.data.repository.GithubRepositoryImpl
import gcd.testing.firstproject.userinformation.domain.repository.GithubRepository
import gcd.testing.firstproject.weather.data.datasource.WeatherRemoteDatasource
import gcd.testing.firstproject.weather.data.repository.WeatherRepositoryImpl
import gcd.testing.firstproject.weather.data.service.WeatherClient
import gcd.testing.firstproject.weather.domain.repository.WeatherRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    single<GithubRepository> { GithubRepositoryImpl() }
    single { WeatherClient() }
    single { WeatherRemoteDatasource(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
    
    // Dollar dependencies
    single<DollarDao> { get<AppDatabase>().getDao() }
    single { DollarLocalDataSource(get()) }
    single<DollarRepository> { DollarRepositoryImpl(get()) }

    singleOf(::RealTimeDataBase)
    single<ExchangeRepository> { ExchangeRepositoryImpl(get()) }
}