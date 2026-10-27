package gcd.testing.firstproject.collaborativetasks.data.preferences

import android.content.Context
import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskNotificationPreferences

class SharedPreferencesTaskNotificationPreferences(context: Context) : TaskNotificationPreferences {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun notificationsMuted(): Boolean = preferences.getBoolean(KEY_MUTED, false)

    override fun setNotificationsMuted(muted: Boolean) {
        preferences.edit().putBoolean(KEY_MUTED, muted).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "task_notifications"
        const val KEY_MUTED = "muted"
    }
}
