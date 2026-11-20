package gcd.testing.firstproject

import android.app.Application
import gcd.testing.firstproject.biometrics.data.security.CurrentActivityHolder
import gcd.testing.firstproject.scheduler.LogScheduler

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CurrentActivityHolder.register(this)
        initKoinAndroid(this)

        val logScheduler = LogScheduler(this)
        logScheduler.schedulePeriodicalUpload()
    }
}