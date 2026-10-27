package gcd.testing.firstproject.collaborativetasks.domain.repository

interface TaskNotificationPreferences {
    fun notificationsMuted(): Boolean

    fun setNotificationsMuted(muted: Boolean)
}
