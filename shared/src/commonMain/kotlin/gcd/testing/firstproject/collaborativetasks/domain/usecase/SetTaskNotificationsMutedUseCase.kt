package gcd.testing.firstproject.collaborativetasks.domain.usecase

import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskNotificationPreferences

class SetTaskNotificationsMutedUseCase(
    private val preferences: TaskNotificationPreferences,
) {
    operator fun invoke(muted: Boolean) {
        preferences.setNotificationsMuted(muted)
    }
}
