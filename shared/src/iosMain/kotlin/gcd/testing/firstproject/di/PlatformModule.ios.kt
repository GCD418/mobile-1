package gcd.testing.firstproject.di

import eu.anifantakis.lib.ksafe.KSafe
import gcd.testing.firstproject.config.AppDatabase
import gcd.testing.firstproject.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> {
        getDatabaseBuilder().build()
    }
    single { KSafe() }
}
