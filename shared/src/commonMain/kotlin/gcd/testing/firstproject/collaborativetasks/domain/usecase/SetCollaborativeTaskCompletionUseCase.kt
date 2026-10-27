package gcd.testing.firstproject.collaborativetasks.domain.usecase

import gcd.testing.firstproject.collaborativetasks.domain.repository.CollaborativeTaskRepository

class SetCollaborativeTaskCompletionUseCase(
    private val repository: CollaborativeTaskRepository,
) {
    suspend operator fun invoke(taskId: String, isCompleted: Boolean) {
        repository.setTaskCompletion(taskId, isCompleted)
    }
}
