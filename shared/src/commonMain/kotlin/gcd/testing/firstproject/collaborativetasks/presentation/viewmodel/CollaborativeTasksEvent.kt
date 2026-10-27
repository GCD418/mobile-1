package gcd.testing.firstproject.collaborativetasks.presentation.viewmodel

sealed interface CollaborativeTasksEvent {
    data class CompletionChanged(
        val taskId: String,
        val isCompleted: Boolean,
    ) : CollaborativeTasksEvent
}
