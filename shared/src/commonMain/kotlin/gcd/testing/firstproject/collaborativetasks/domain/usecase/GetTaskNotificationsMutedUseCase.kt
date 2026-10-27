package gcd.testing.firstproject.collaborativetasks.domain.usecase

import gcd.testing.firstproject.collaborativetasks.domain.repository.TaskNotificationPreferences

class GetTaskNotificationsMutedUseCase(
    private val preferences: TaskNotificationPreferences,
) {
    operator fun invoke(): Boolean = preferences.notificationsMuted()
}
