package gcd.testing.firstproject

import android.content.Context
import gcd.testing.firstproject.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

fun initKoinAndroid(context: Context) {
    startKoin {
        androidContext(context)
        androidLogger()
        modules(sharedModule())
    }
}