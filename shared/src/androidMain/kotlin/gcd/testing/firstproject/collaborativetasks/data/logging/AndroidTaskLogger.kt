package gcd.testing.firstproject.collaborativetasks.data.logging

import android.util.Log

class AndroidTaskLogger : TaskLogger {
    override fun error(message: String, throwable: Throwable) {
        Log.e(TAG, message, throwable)
    }

    private companion object {
        const val TAG = "CollaborativeTasks"
    }
}
