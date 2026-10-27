package gcd.testing.firstproject.collaborativetasks.domain.usecase

import gcd.testing.firstproject.collaborativetasks.domain.model.CollaborativeTask
import gcd.testing.firstproject.collaborativetasks.domain.repository.CollaborativeTaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollaborativeTasksUseCase(
    private val repository: CollaborativeTaskRepository,
) {
    operator fun invoke(): Flow<List<CollaborativeTask>> = repository.observeTasks()
}
