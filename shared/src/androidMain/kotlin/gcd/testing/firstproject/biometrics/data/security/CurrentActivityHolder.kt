package gcd.testing.firstproject.biometrics.data.security

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

object CurrentActivityHolder : Application.ActivityLifecycleCallbacks {
    private var isRegistered = false
    private var activityRef: WeakReference<FragmentActivity>? = null

    fun register(application: Application) {
        if (isRegistered) return
        isRegistered = true
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getCurrentActivity(): FragmentActivity? = activityRef?.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is FragmentActivity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        if (activity is FragmentActivity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityResumed(activity: Activity) {
        if (activity is FragmentActivity) {
            activityRef = WeakReference(activity)
        }
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (activityRef?.get() === activity) {
            activityRef?.clear()
            activityRef = null
        }
    }
}
