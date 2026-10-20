package gcd.testing.firstproject

import android.app.Application
import gcd.testing.firstproject.scheduler.LogScheduler

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)

        val logScheduler = LogScheduler(this)
        logScheduler.schedulePeriodicalUpload()
    }
}