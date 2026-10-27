package gcd.testing.firstproject.collaborativetasks.presentation.viewmodel

sealed interface CollaborativeTasksEvent {
    data object ToggleNotificationMute : CollaborativeTasksEvent

    data class CompletionChanged(
        val taskId: String,
        val isCompleted: Boolean,
    ) : CollaborativeTasksEvent
}
