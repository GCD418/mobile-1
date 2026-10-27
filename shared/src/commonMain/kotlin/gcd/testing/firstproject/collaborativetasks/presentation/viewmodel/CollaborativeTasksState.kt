package gcd.testing.firstproject.collaborativetasks.presentation.viewmodel

import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask

data class CollaborativeTasksState(
    val tasks: List<CollaborativeTask> = emptyList(),
    val completedCount: Int = 0,
    val pendingTaskIds: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
)
