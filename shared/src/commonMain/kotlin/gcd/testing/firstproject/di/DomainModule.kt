package gcd.testing.firstproject.di

import gcd.testing.firstproject.userinformation.domain.usecase.FindAliasUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::FindAliasUseCase) //Esto declara un singleton
}