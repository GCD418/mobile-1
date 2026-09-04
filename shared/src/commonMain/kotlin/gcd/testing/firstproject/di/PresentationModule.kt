package gcd.testing.firstproject.di

import gcd.testing.firstproject.signin.presentation.viewmodel.LoginViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(
        ::LoginViewModel
    )
}
