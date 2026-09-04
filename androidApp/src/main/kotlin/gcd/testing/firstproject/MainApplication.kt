package gcd.testing.firstproject

import android.app.Application

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}